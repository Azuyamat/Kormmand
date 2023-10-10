package command

import Manager
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

class CommandManager(kord: Kord) : Manager {
    override val name = "CommandManager"

    private val bot = kord
    private val commands: MutableMap<String, Command> = mutableMapOf()
    private val guildCommands: MutableMap<String, MutableMap<String, Command>> = mutableMapOf()

    init {
        bot.on<GuildChatInputCommandInteractionCreateEvent> {
            handleInteraction(this)
        }
    }

    suspend fun registerCommands(commands : List<Command>, guildId: String? = null) {
        val startTimeCommand = System.currentTimeMillis()

        if (guildId == null) {
            bot.createGlobalApplicationCommands {
                for (command in commands) {
                    registerCommandToList(command.name, command)
                    input(command.name, command.description, command.builder)
                }
            }
        }
        else {
            bot.createGuildApplicationCommands(Snowflake(guildId)) {
                for (command in commands) {
                    registerCommandToList(command.name, command, guildId)
                    input(command.name, command.description, command.builder)
                }
            }
        }

        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTimeCommand

        logMessage("Registered ${commands.size} commands (${elapsedTime}ms)")
    }

    suspend fun registerCommand(command: Command, guildId: String? = null){
        registerCommandToList(command.name, command, guildId)
        if (guildId == null)
            bot.createGlobalChatInputCommand(command.name, command.description, command.builder)
        else
            bot.createGuildApplicationCommands(Snowflake(guildId)) {
                input(command.name, command.description, command.builder)
            }
    }

    fun getCommands() : MutableMap<String, Command> {
        return commands
    }
    fun getGuildCommands(guildId: String) : MutableMap<String, Command>? {
        return guildCommands[guildId]
    }


    private fun registerCommandToList(name: String, command: Command, guildId: String? = null) {
        if (guildId == null)
            commands[name] = command
        else
            guildCommands.getOrPut(guildId) { mutableMapOf() }[name] = command
        logMessage("Registered ${if (guildId == null) "global" else "guild"} command: $name")
    }

    private suspend fun handleInteraction(event: GuildChatInputCommandInteractionCreateEvent) {
        val interaction = event.interaction
        val commandName = interaction.invokedCommandName

        val command = commands[commandName]
        if (command != null) command.executePerm(event)
        else {
            val guildId = interaction.guildId.toString()
            val guildCommand = guildCommands[guildId]?.get(commandName)
            if (guildCommand != null) guildCommand.executePerm(event)
            else interaction.respondEphemeral { content = "Command not found" }
        }
    }
}