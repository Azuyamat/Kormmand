package com.azuyamat

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.event.interaction.*
import dev.kord.rest.builder.interaction.*
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter

suspend fun InteractionCreateEvent.sendMessage(text: String) = when (this) {
    is ChatInputCommandInteractionCreateEvent -> interaction.respondEphemeral { content = text }
    is ButtonInteractionCreateEvent -> interaction.respondEphemeral { content = text }
    is SelectMenuInteractionCreateEvent -> interaction.respondEphemeral { content = text }
    is ModalSubmitInteractionCreateEvent -> interaction.respondEphemeral { content = text }
    else -> null
}

fun InteractionCreateEvent.getClassType() = when (this) {
    is ChatInputCommandInteractionCreateEvent -> "Command"
    is ButtonInteractionCreateEvent -> "Button"
    is SelectMenuInteractionCreateEvent -> "SelectMenu"
    is ModalSubmitInteractionCreateEvent -> "Modal"
    else -> null
}

fun BaseInputChatBuilder.addOption(type: KClassifier, name: String, description: String, required: Boolean = true) = when (type) {
    String::class -> string(name, description) { this.required = required }
    Int::class -> integer(name, description) { this.required = required }
    Boolean::class -> boolean(name, description) { this.required = required }
    Member::class -> user(name, description) { this.required = required }
    Attachment::class -> attachment(name, description) { this.required = required }
    Channel::class -> channel(name, description) { this.required = required }
    Entity::class -> mentionable(name, description) { this.required = required }
    Role::class -> role(name, description) { this.required = required }
    User::class -> user(name, description) { this.required = required }
    Double::class -> number(name, description) { this.required = required }
    else -> null
}

fun ChatInputCreateBuilder.addSubcommand(name: String, description: String, other: SubCommandBuilder.() -> Unit) = subCommand(name, description, other)

suspend fun KClassifier.getOptionFromClass(event: ChatInputCommandInteractionCreateEvent, param: KParameter) = when (this) {
    String::class -> event.interaction.command.strings[param.name ?: ""]
    Int::class -> event.interaction.command.integers[param.name ?: ""]
    Boolean::class -> event.interaction.command.booleans[param.name ?: ""]
    Member::class -> event.interaction.command.members[param.name ?: ""]?.asMemberOrNull()
    Attachment::class -> event.interaction.command.attachments[param.name ?: ""]
    Channel::class -> event.interaction.command.channels[param.name ?: ""]?.asChannel()
    Entity::class -> event.interaction.command.mentionables[param.name ?: ""]
    Role::class -> event.interaction.command.roles[param.name ?: ""]
    User::class -> event.interaction.command.users[param.name ?: ""]
    Double::class -> event.interaction.command.numbers[param.name ?: ""]
    GuildChatInputCommandInteractionCreateEvent::class -> event
    else -> null
}