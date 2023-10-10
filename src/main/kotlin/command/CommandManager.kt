package command

import Manager
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on

class CommandManager(kord: Kord) : Manager {
    override val name = "CommandManager"

    private val bot = kord
    private val commands: MutableMap<String, Command> = mutableMapOf()

    init {
        bot.on<GuildChatInputCommandInteractionCreateEvent> {
            handleInteraction(this)
        }
    }

    suspend fun registerCommands(commands : List<Command>) {
        val startTimeCommand = System.currentTimeMillis()

        bot.createGlobalApplicationCommands {
            for (command in commands) {
                registerCommandToList(command.name, command)
                input(command.name, command.description, command.builder)
            }
        }
        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTimeCommand

        logMessage("Registered ${commands.size} commands (${elapsedTime}ms)")
    }

    suspend fun registerCommand(command: Command){
        registerCommandToList(command.name, command)
        bot.createGlobalChatInputCommand(command.name, command.description, command.builder)
    }

    fun getCommands() : MutableMap<String, Command> {
        return commands
    }


    private fun registerCommandToList(name: String, command: Command) {
        logMessage("Registered command: $name")
        commands[name] = command
    }

    private suspend fun handleInteraction(interaction: GuildChatInputCommandInteractionCreateEvent) {
        val talk = interaction.interaction
        val commandName = talk.invokedCommandName

        val command = commands[commandName]
        if (command != null) command.executePerm(interaction)
        else talk.respondEphemeral { content = "Command not found" }
    }
}