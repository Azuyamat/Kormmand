import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent

interface Interaction<T : InteractionCreateEvent> {
    val id : String?
    val name : String?
    val description : String?
    val permission : Permission
        get() = Permission.ViewChannel

    suspend fun executePerm(event: T) {
        try {
            val interaction = event.interaction
            val guildId = interaction.data.guildId.value
            if (guildId == null) {
                execute(event)
                return
            }
            val member = interaction.user.asMember(guildId)
            if (member.getPermissions().contains(permission))
                execute(event)
            else
                noPermission(event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun execute(event: InteractionCreateEvent)

    suspend fun noPermission(event: InteractionCreateEvent){
        when (event) {
            is GuildChatInputCommandInteractionCreateEvent -> event.interaction.respondEphemeral { content = "You don't have permission to use this command." }
            is SelectMenuInteractionCreateEvent -> event.interaction.respondEphemeral { content = "You don't have permission to use this select menu." }
            is ButtonInteractionCreateEvent -> event.interaction.respondEphemeral { content = "You don't have permission to use this button." }
            else -> return
        }
    }
}