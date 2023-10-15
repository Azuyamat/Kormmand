package com.azuyamat.interactions

import com.azuyamat.KordInteraction
import com.azuyamat.getOptionFromClass
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

interface Command : KordInteraction<GuildChatInputCommandInteractionCreateEvent> {
    /**
     * Do not override this function
     */
    override suspend fun execute(event: GuildChatInputCommandInteractionCreateEvent){
        val mainFunc = this::class.memberFunctions.find { it.name == "main" }?:return
        val options = mainFunc.valueParameters.mapNotNull { p -> p.type.classifier?.getOptionFromClass(event, p) }.toTypedArray()
        if (mainFunc.isSuspend) mainFunc.callSuspend(this, event, *options)
        else mainFunc.call(this, event, *options)
    }
}