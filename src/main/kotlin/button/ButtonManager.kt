package button

import Manager
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.on

class ButtonManager(kord: Kord) : Manager {
    override val name = "ButtonManager"

    private val bot = kord
    private val buttons: MutableMap<String, Button> = mutableMapOf()


    init {
        bot.on<ButtonInteractionCreateEvent> {
            handleInteraction(this)
        }
    }

    fun registerButtons(buttons : List<Button>) {
        val startTime = System.currentTimeMillis()

        for (button in buttons) {
            registerButton(button)
        }

        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime

        logMessage("Registered ${buttons.size} buttons (${elapsedTime}ms)")
    }

    private fun registerButton(button: Button){
        registerCommandToList(button.id, button)
        logMessage("Registered button: ${button.name ?: button.id}")
    }

    fun getButtons() : MutableMap<String, Button> {
        return buttons
    }


    private fun registerCommandToList(id: String, command: Button) {
        buttons[id] = command
    }

    private suspend fun handleInteraction(interaction: ButtonInteractionCreateEvent) {
        val talk = interaction.interaction
        val id = talk.componentId.split("//")[0]

        val button = buttons[id]
        if (button != null) button.executePerm(interaction)
        else talk.respondEphemeral { content = "Button not found" }
    }
}