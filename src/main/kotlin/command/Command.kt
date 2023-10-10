package command

import Interaction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder

/**
 * @author Azuyamat
 */

interface Command : Interaction<GuildChatInputCommandInteractionCreateEvent> {
    override val name: String
    override val description: String
    val builder: GlobalChatInputCreateBuilder.() -> Unit
        get() = {}
}