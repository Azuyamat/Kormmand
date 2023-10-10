package select

import Interaction
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent

interface SelectMenu : Interaction<SelectMenuInteractionCreateEvent> {
    override val id: String
}