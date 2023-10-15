package interactions

import com.azuyamat.CommandClass
import com.azuyamat.Description
import com.azuyamat.RequirePermission
import com.azuyamat.interactions.Command
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import discord4j.core.`object`.entity.User
import discord4j.rest.util.Permission

@CommandClass("Jeff")
@RequirePermission(Permission.ADMINISTRATOR)
object JeffCommand : Command {
    fun main(@Description("the user to mention") string: User, event: ApplicationCommandInteractionEvent) {
        event.reply("Jeff is ${string.username}").withComponents(
            ActionRow.of(Button.primary("jeff", "Jeff"))
        ).block()
    }
}