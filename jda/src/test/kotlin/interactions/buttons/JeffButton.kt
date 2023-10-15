package interactions.buttons

import com.azuyamat.InteractionClass
import com.azuyamat.RequirePermission
import com.azuyamat.interactions.Button
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

@InteractionClass
@RequirePermission(Permission.ADMINISTRATOR)
object JeffButton : Button {
    override suspend fun execute(event: ButtonInteractionEvent) {
        event.replyModal(
            Modal.create("jeff", "Jeff")
                .addActionRow(
                    TextInput.create("jeff", "Jeff", TextInputStyle.SHORT).build()
                )
                .build())
            .queue()
    }
}