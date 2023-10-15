package com.azuyamat.interactions

import com.azuyamat.D4JInteraction
import com.azuyamat.getOptionTypeFromClass
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

interface Command : D4JInteraction<ApplicationCommandInteractionEvent> {
    /**
     * Do not override this function
     */
    override suspend fun execute(event: ApplicationCommandInteractionEvent) {
        try {
            val mainFunc = this::class.memberFunctions.find { it.name == "main" } ?: return
            val options = mainFunc.valueParameters.mapNotNull { p -> p.type.classifier?.getOptionTypeFromClass(event, p) }.toTypedArray()
            if (mainFunc.isSuspend) mainFunc.callSuspend(this, event, *options)
            else mainFunc.call(this, *options)
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                event.reply("An error occurred while executing this command").block()
            }
            e.printStackTrace()
        }
    }
}