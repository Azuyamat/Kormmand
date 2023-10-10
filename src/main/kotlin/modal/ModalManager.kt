package modal

import Manager
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.on

class ModalManager(kord: Kord) : Manager {
    override val name = "ModalManager"

    private val bot = kord
    private val modals: MutableMap<String, Modal> = mutableMapOf()


    init {
        bot.on<ModalSubmitInteractionCreateEvent> {
            handleInteraction(this)
        }
    }

    fun registerModals(modals : List<Modal>) {
        val startTime = System.currentTimeMillis()

        for (modal in modals) {
            registerModal(modal)
        }

        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime

        logMessage("Registered ${modals.size} modals (${elapsedTime}ms)")
    }

    private fun registerModal(modal: Modal){
        registerCommandToList(modal.id, modal)
        logMessage("Registered modal: ${modal.name ?: modal.id}")
    }

    fun getModals() : MutableMap<String, Modal> {
        return modals
    }


    private fun registerCommandToList(id: String, modal: Modal) {
        modals[id] = modal
    }

    private suspend fun handleInteraction(event: ModalSubmitInteractionCreateEvent) {
        val interaction = event.interaction
        val id = interaction.modalId.split("//")[0]

        val modal = modals[id]
        if (modal != null) modal.executePerm(event)
        else interaction.respondEphemeral { content = "Button not found" }
    }
}