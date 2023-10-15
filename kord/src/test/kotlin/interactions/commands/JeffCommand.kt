package interactions.commands

import com.azuyamat.CommandClass
import com.azuyamat.interactions.Command
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent

@CommandClass("Jeff")
object JeffCommand : Command {
    suspend fun main(event: GuildChatInputCommandInteractionCreateEvent, string: String) {
        event.interaction.respondEphemeral { content = string }
    }
}