package interactions.commands

import com.azuyamat.CommandClass
import com.azuyamat.GuildOnly
import com.azuyamat.IsSubcommand
import com.azuyamat.RequirePermission
import com.azuyamat.interactions.Command
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

@CommandClass("Jeff command")
@RequirePermission(Permission.ADMINISTRATOR)
@GuildOnly("1087197549275906058")
object JoeCommand : Command {
    fun main(event: SlashCommandInteractionEvent) {
        event.reply("jeff")
            .addActionRow(
                Button.primary("jeff", "jeff")
            )
            .queue()
    }

    @IsSubcommand("jeff")
    fun jeff(event: SlashCommandInteractionEvent) {
        event.reply("jeff")
            .addActionRow(
                Button.primary("jeff", "jeff")
            )
            .queue()
    }
}