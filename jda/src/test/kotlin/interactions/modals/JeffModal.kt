package interactions.modals

import com.azuyamat.GuildOnly
import com.azuyamat.InteractionClass
import com.azuyamat.interactions.Modal
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

@InteractionClass
@GuildOnly("1087197549275906058")
object JeffModal : Modal {
    override suspend fun execute(event: ModalInteractionEvent) {
        event.reply("Jeff")
            .addActionRow(
                StringSelectMenu.create("jeff")
                    .addOption("Jeff", "jeff")
                    .build()
            )
            .queue()
    }
}