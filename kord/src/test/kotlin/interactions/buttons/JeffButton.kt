package interactions.buttons

import com.azuyamat.InteractionClass
import com.azuyamat.interactions.Button
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent

@InteractionClass
object JeffButton : Button {
    override suspend fun execute(event: ButtonInteractionCreateEvent) {
        event.interaction.respondEphemeral { content = "Jeff" }
    }
}