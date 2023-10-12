import dev.kord.common.entity.Permission
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.*

annotation class InteractionClass()

interface Interaction<T : InteractionCreateEvent> {
    val id: String?
        get() = null
    val name: String?
        get() = null
    val description: String?
        get() = null
    val permission: Permission
        get() = Permission.ViewChannel

    suspend fun executePerm(event: InteractionCreateEvent) {
        try {
            val e = event as T
            val interaction = e.interaction
            val guildId = interaction.data.guildId.value
            if (guildId == null) {
                execute(e)
                return
            }
            val member = interaction.user.asMember(guildId)
            if (member.getPermissions().contains(permission))
                execute(e)
            else
                noPermission(e)
        } catch (e: Exception) {
            when (event) {
                is ChatInputCommandInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Command got an error" }
                is ButtonInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Button got an error" }
                is SelectMenuInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Select got an error" }
                is ModalSubmitInteractionCreateEvent -> event.interaction.respondEphemeral { content = "Modal got an error" }
                else -> return
            }
            e.printStackTrace()
        }
    }

    suspend fun execute(event: T)

    suspend fun noPermission(event: InteractionCreateEvent) {
        when (event) {
            is GuildChatInputCommandInteractionCreateEvent -> event.interaction.respondEphemeral { content = "You don't have permission to use this command." }
            is SelectMenuInteractionCreateEvent -> event.interaction.respondEphemeral { content = "You don't have permission to use this select menu." }
            is ButtonInteractionCreateEvent -> event.interaction.respondEphemeral { content = "You don't have permission to use this button." }
            else -> return
        }
    }
}