package command

import Interaction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder

annotation class CommandClass

interface Command : Interaction<GuildChatInputCommandInteractionCreateEvent> {
    override val name: String
    override val description: String
    val builder: ChatInputCreateBuilder.() -> Unit
        get() = {}
}