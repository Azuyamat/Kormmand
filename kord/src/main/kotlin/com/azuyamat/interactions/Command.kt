package com.azuyamat.interactions

import com.azuyamat.KordCommand
import com.azuyamat.KordInteraction
import com.azuyamat.getOptionFromClass
import com.azuyamat.utils.firstWord
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible

interface Command : KordCommand {
    /**
     * Do not override this function
     */
    override suspend fun execute(event: GuildChatInputCommandInteractionCreateEvent){
        val c = event.interaction.command
        val groupName = when (c) {
            is RootCommand, is SubCommand -> null
            is GroupCommand -> c.groupName
        }
        val subCommandName = when (c) {
            is RootCommand -> null
            is SubCommand -> c.name
            is GroupCommand -> c.name
        }

        var function = getMainFun()
        if (subCommandName != null) {
            function = if (groupName != null)
                getClasses().first { it.simpleName?.firstWord() == groupName }.memberFunctions.first { it.name == subCommandName }
            else
                getOtherFuns().first { it.name == subCommandName }
        }

        println(function)

        val options = function.valueParameters.mapNotNull { it.type.classifier?.getOptionFromClass(event, it)}.toTypedArray()
        for (option in options) {
            println(option)
        }
        try {
            val declareClass = if (function.isAccessible) this else getClasses().first { it.simpleName?.firstWord() == groupName }.objectInstance!!
            if (function.isSuspend) function.callSuspend(declareClass, *options)
            else function.call(declareClass, *options)
        } catch (e: Exception) {
            println("Failed to execute command: ${e.message}")
            e.printStackTrace()
        }
    }
}