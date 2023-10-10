package modal

import Interaction
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent

interface Modal : Interaction<ModalSubmitInteractionCreateEvent> {
    override val id: String
}