package com.azuyamat.interactions

import com.azuyamat.JDAInteraction
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent

interface SelectMenu : JDAInteraction<GenericSelectMenuInteractionEvent<*, *>>