package modal

import Manager
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.on
import select.SelectMenu

class ModalManager(kord: Kord) : Manager<Modal, ModalSubmitInteractionCreateEvent> {
    override val name = "ModalManager"
    override val bot: Kord = kord
    override val interactions: MutableMap<String, Modal> = mutableMapOf()
    override val guildInteractions: MutableMap<String, MutableMap<String, Modal>> = mutableMapOf()

    init {
        bot.on<ModalSubmitInteractionCreateEvent> {
            handleInteraction(this)
        }
    }
}