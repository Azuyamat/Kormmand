package interactions.selectMenus

import com.azuyamat.InteractionClass
import com.azuyamat.interactions.SelectMenu
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent

@InteractionClass
object JeffSelectMenu : SelectMenu {
    override suspend fun execute(event: GenericSelectMenuInteractionEvent<*, *>) {
        event.reply("Jeff").queue()
    }
}