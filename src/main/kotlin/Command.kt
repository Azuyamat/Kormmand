import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder

/**
 * @author Azuyamat
 */

interface Command {
    val name: String
    val description: String
    val permission: Permission?
        get() = null
    val builder: GlobalChatInputCreateBuilder.() -> Unit
        get() = {}


    suspend fun executePerm(event: GuildChatInputCommandInteractionCreateEvent){
        try {
            val talk = event.interaction
            if (permission == null || talk.user.getPermissions().contains(permission!!))
                execute(event)
            else
                talk.respondEphemeral { content = "You don't have permission to use this command." }
        } catch (e: Exception) {
            event.interaction.respondEphemeral { content = "An error occurred while executing this command." }
            e.printStackTrace()
        }
    }

    suspend fun execute(event: GuildChatInputCommandInteractionCreateEvent)
}