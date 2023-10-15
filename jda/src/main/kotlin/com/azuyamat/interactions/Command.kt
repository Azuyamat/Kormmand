package com.azuyamat.interactions

import com.azuyamat.JDAInteraction
import com.azuyamat.getOptionFromClass
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

interface Command : JDAInteraction<SlashCommandInteractionEvent> {
    /**
     * Do not override this function
     */
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val mainFunc = this::class.memberFunctions.find { it.name == "main" } ?: return
        val options = mainFunc.valueParameters.mapNotNull { p -> p.type.classifier?.getOptionFromClass(event, p) }.toTypedArray()
        if (mainFunc.isSuspend) mainFunc.callSuspend(this, event, *options)
        else mainFunc.call(this, event, *options)
    }
}