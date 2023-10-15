package com.azuyamat

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter

fun getOptionTypeClass(c: KClassifier?) = when(c) {
    String::class -> OptionType.STRING
    Int::class -> OptionType.INTEGER
    Boolean::class -> OptionType.BOOLEAN
    Message.Attachment::class -> OptionType.ATTACHMENT
    Channel::class -> OptionType.CHANNEL
    IMentionable::class -> OptionType.MENTIONABLE
    Role::class -> OptionType.ROLE
    User::class -> OptionType.USER
    Double::class -> OptionType.NUMBER
    else -> null
}

fun GenericInteractionCreateEvent.sendMessage(text: String) = when (this) {
    is SlashCommandInteractionEvent -> reply(text).setEphemeral(true).queue()
    is ButtonInteractionEvent -> reply(text).setEphemeral(true).queue()
    is GenericSelectMenuInteractionEvent<*, *> -> reply(text).setEphemeral(true).queue()
    is ModalInteractionEvent -> reply(text).setEphemeral(true).queue()
    else -> null
}

fun GenericInteractionCreateEvent.getTypedId() = when (this) {
    is SlashCommandInteractionEvent -> name
    is ButtonInteractionEvent -> componentId
    is GenericSelectMenuInteractionEvent<*, *> -> componentId
    is ModalInteractionEvent -> modalId
    else -> null
}

fun GenericInteractionCreateEvent.getClassType() = when (this) {
    is SlashCommandInteractionEvent -> "Command"
    is ButtonInteractionEvent -> "Button"
    is GenericSelectMenuInteractionEvent<*, *> -> "SelectMenu"
    is ModalInteractionEvent -> "Modal"
    else -> null
}


fun KClassifier.getOptionFromClass(event: SlashCommandInteractionEvent, param: KParameter) = when (this) {
    String::class -> event.getOption(param.name?:"")?.asString
    Int::class -> event.getOption(param.name?:"")?.asLong?.toInt()
    Boolean::class -> event.getOption(param.name?:"")?.asBoolean
    Message.Attachment::class -> event.getOption(param.name?:"")?.asAttachment
    Channel::class -> event.getOption(param.name?:"")?.asChannel
    IMentionable::class -> event.getOption(param.name?:"")?.asMentionable
    Role::class -> event.getOption(param.name?:"")?.asRole
    User::class -> event.getOption(param.name?:"")?.asUser
    Double::class -> event.getOption(param.name?:"")?.asDouble
    else -> null
}