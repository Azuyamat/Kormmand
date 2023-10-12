import command.Command
import command.CommandClass
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.interaction.*
import org.reflections.Reflections
import utils.log
import utils.cyan

interface Manager<T : Interaction<*>, E : InteractionCreateEvent> {
    val bot: Kord
    val name: String

    val interactions: MutableMap<String, T>
    val guildInteractions: MutableMap<String, MutableMap<String, T>>

    suspend fun registerInteractions(prefix: String, guildId: String? = null) {
        val startTime = System.currentTimeMillis()
        val type = name.lowercase().replace("manager", "")

        logMessage("Registering ${type + "s"}...")

        var hasCommands = false

        Reflections(prefix).getTypesAnnotatedWith(InteractionClass::class.java).forEach {
            if (!hasCommands) hasCommands = it.isAnnotationPresent(CommandClass::class.java)
            val interaction = it.getConstructor().newInstance() as T
            registerInteraction(interaction, guildId)
        }

        if (hasCommands) registerCommands()

        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime

        logMessage("Registered ${interactions.size} ${type + "s"} (${elapsedTime}ms)")
    }

    private suspend fun registerCommands() {
        bot.createGlobalApplicationCommands {
            for (interaction in interactions) {
                val command = interaction.value as Command
                input(command.name, command.description, command.builder)
            }
        }
        for (guild in guildInteractions) {
            bot.createGuildApplicationCommands(Snowflake(guild.key)) {
                for (interaction in guild.value) {
                    val command = interaction.value as Command
                    input(command.name, command.description, command.builder)
                }
            }
        }
    }

    private fun registerInteraction(interaction: T, guildId: String? = null) {
        val identifier: String = interaction.id ?: interaction.name ?: return
        if (guildId == null)
            interactions[identifier] = interaction
        else
            guildInteractions.getOrPut(guildId) { mutableMapOf() }[identifier] = interaction
        logMessage("Registered $identifier")
    }

    suspend fun handleInteraction(event: E) {
        val talk = event.interaction
        var id = talk.data.data.customId.value ?: talk.data.data.name.value ?: return
        id = id.split("//")[0]

        val interaction = interactions[id]
        if (interaction != null) interaction.executePerm(event)
        else {
            val channel = talk.getChannel() as TextChannel
            val guildId = channel.guildId.toString()
            val guildCommand = guildInteractions[guildId]?.get(id)
            if (guildCommand != null) guildCommand.executePerm(event)
            else {
                when (event) {
                    is ChatInputCommandInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Command not found" }
                    is ButtonInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Button not found" }
                    is SelectMenuInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Select menu not found" }
                    is ModalSubmitInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Modal not found" }
                    else -> logMessage("Error occurred while handling interaction")
                }
            }
        }
    }

    fun logMessage(message: String) = log(cyan("[${name.uppercase()}] ") + message)
}