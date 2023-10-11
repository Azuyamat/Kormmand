package button

import Manager
import command.Command
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.on

class ButtonManager(kord: Kord) : Manager<Button, ButtonInteractionCreateEvent> {
    override val name = "ButtonManager"
    override val bot: Kord = kord
    override val interactions: MutableMap<String, Button> = mutableMapOf()
    override val guildInteractions: MutableMap<String, MutableMap<String, Button>> = mutableMapOf()

    init {
        bot.on<ButtonInteractionCreateEvent> {
            handleInteraction(this)
        }
    }
}