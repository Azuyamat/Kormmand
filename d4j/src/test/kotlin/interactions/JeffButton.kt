package interactions

import com.azuyamat.InteractionClass
import com.azuyamat.interactions.Button
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@InteractionClass
object JeffButton : Button {
    override suspend fun execute(event: ButtonInteractionEvent) {
        withContext(Dispatchers.IO) {
            event.reply("Jeff is").block()
        }
    }
}