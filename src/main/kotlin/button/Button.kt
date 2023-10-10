package button

import Interaction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent

interface Button : Interaction<ButtonInteractionCreateEvent> {
    override val id: String
}