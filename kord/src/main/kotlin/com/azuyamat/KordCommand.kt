package com.azuyamat

import com.azuyamat.utils.firstWord
import dev.kord.common.entity.Permissions
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.input
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

interface KordCommand : CommandInterface<GuildChatInputCommandInteractionCreateEvent> ,KordInteraction<GuildChatInputCommandInteractionCreateEvent> {
    fun addToBuilder(multi: MultiApplicationCommandBuilder) : MultiApplicationCommandBuilder {
        lateinit var builder: ChatInputCreateBuilder

        println("-> $simpleName")

        multi.input(simpleName, description) {
            defaultMemberPermissions = Permissions(permission)
            builder = this
        }

        //Loop params of main function to add them as options
        getMainFun().valueParameters.forEach { p ->
            builder.addOption(
                p.type.classifier ?: return@forEach,
                p.name ?: return@forEach,
                p.name ?: return@forEach,
                !p.isOptional
            )
        }

        //Add subcommand groups
        getClasses().forEach {
            val subCDescription = it.findAnnotation<IsSubcommand>()?.description ?: return@forEach
            println("   --> ${it.simpleName}")
            builder.group(it.simpleName?.firstWord() ?: return@forEach, subCDescription) {
                it.memberFunctions.forEach subC@ { subC ->
                    val subSubCDescription = subC.findAnnotation<IsSubcommand>()?.description ?: return@subC
                    println("       ---> ${subC.name}")
                    subCommand(subC.name, subSubCDescription) {
                        subC.valueParameters.forEach params@ { p ->
                            addOption(
                                p.type.classifier ?: return@params,
                                p.name ?: return@params,
                                p.name ?: return@params,
                                !p.isOptional
                            )
                        }
                    }
                }
            }
        }

        //Loop params of other functions to add them as options
        getOtherFuns().forEach { subC ->
            println("   --> ${subC.name}")
            val subCDescription = subC.findAnnotation<IsSubcommand>()?.description ?: return@forEach
            builder.addSubcommand(subC.name, subCDescription) {
                subC.valueParameters.forEach params@ { p ->
                    addOption(
                        p.type.classifier ?: return@params,
                        p.name ?: return@params,
                        p.name ?: return@params,
                        !p.isOptional
                    )
                }
            }
        }

        return multi
    }
}