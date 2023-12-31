package interactions.commands

import com.azuyamat.CommandClass
import com.azuyamat.IsSubcommand
import com.azuyamat.interactions.Command
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.message.create.actionRow

@CommandClass("Jeff")
object PoopCommand : Command {
    suspend fun main(event: GuildChatInputCommandInteractionCreateEvent) {
        event.interaction.respondEphemeral { content = "Jeff" }
    }

    @IsSubcommand("fun")
    object View : Command {

        @IsSubcommand("fun")
        suspend fun all(event: GuildChatInputCommandInteractionCreateEvent, string: String) {
            event.interaction.respondEphemeral { content = string }
        }
    }

    @IsSubcommand("fun")
    suspend fun user(event: GuildChatInputCommandInteractionCreateEvent) {
        event.interaction.respondEphemeral { content = "none" }.createEphemeralFollowup {
            actionRow {
                interactionButton(
                    ButtonStyle.Secondary,
                    "jeff"
                ) {
                    label = "Jeff"
                }
            }
        }
    }
}