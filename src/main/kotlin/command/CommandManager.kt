package command

import Manager
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

class CommandManager(kord: Kord) : Manager<Command, GuildChatInputCommandInteractionCreateEvent> {
    override val name = "CommandManager"
    override val bot: Kord = kord
    override val interactions: MutableMap<String, Command> = mutableMapOf()
    override val guildInteractions: MutableMap<String, MutableMap<String, Command>> = mutableMapOf()

    init {
        bot.on<GuildChatInputCommandInteractionCreateEvent> {
            handleInteraction(this)
        }
    }

    override suspend fun handleInteraction(event: GuildChatInputCommandInteractionCreateEvent) {
        val interaction = event.interaction
        val commandName = interaction.invokedCommandName

        val command = interactions[commandName]
        if (command != null) command.executePerm(event)
        else {
            val guildId = interaction.guildId.toString()
            val guildCommand = guildInteractions[guildId]?.get(commandName)
            if (guildCommand != null) guildCommand.executePerm(event)
            else interaction.respondEphemeral { content = "Command not found" }
        }
    }
}