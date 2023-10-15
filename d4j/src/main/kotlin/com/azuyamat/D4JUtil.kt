package com.azuyamat

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.*
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.`object`.entity.Attachment
import discord4j.core.`object`.entity.Role
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.Channel
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter

fun getOptionTypeClass(c: KClassifier) = when(c) {
    String::class -> Type.STRING
    Int::class -> Type.INTEGER
    Boolean::class -> Type.BOOLEAN
    Attachment::class -> Type.ATTACHMENT
    Channel::class -> Type.CHANNEL
    Snowflake::class -> Type.MENTIONABLE
    Role::class -> Type.ROLE
    User::class -> Type.USER
    Double::class -> Type.NUMBER
    else -> null
}

fun InteractionCreateEvent.getTypedId() = when (this) {
    is ApplicationCommandInteractionEvent -> commandName
    is ButtonInteractionEvent -> customId
    is SelectMenuInteractionEvent -> customId
    is ModalSubmitInteractionEvent -> customId
    else -> null
}

fun InteractionCreateEvent.sendMessage(text: String) = when (this) {
    is ApplicationCommandInteractionEvent -> reply(text).withEphemeral(true)
    is ButtonInteractionEvent -> reply(text).withEphemeral(true)
    is SelectMenuInteractionEvent -> reply(text).withEphemeral(true)
    is ModalSubmitInteractionEvent -> reply(text).withEphemeral(true)
    else -> null
}

fun InteractionCreateEvent.getClassType() = when (this) {
    is ApplicationCommandInteractionEvent -> "Command"
    is ButtonInteractionEvent -> "Button"
    is SelectMenuInteractionEvent -> "SelectMenu"
    is ModalSubmitInteractionEvent -> "Modal"
    else -> null
}

fun KClassifier.getOptionTypeFromClass(event: ApplicationCommandInteractionEvent, param: KParameter) = when (this) {
    String::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asString()
    Int::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asLong()
    Boolean::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asBoolean()
    Attachment::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asAttachment()
    Channel::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asChannel().block()
    Role::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asRole().block()
    User::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asUser().block()
    Double::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asDouble()
    Snowflake::class -> event.interaction.commandInteraction.get().getOption(param.name?:"").get().value.get().asSnowflake()
    ApplicationCommandInteractionEvent::class -> event
    else -> null
}