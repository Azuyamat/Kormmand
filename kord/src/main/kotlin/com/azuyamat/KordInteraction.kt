package com.azuyamat

import dev.kord.common.entity.Permission
import dev.kord.core.event.interaction.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

annotation class RequirePermission(val permission: KClass<Permission>)

interface KordInteraction<T: InteractionCreateEvent> : Interaction<T> {
    val permission: Permission
        get() = this::class.findAnnotation<RequirePermission>()?.permission?.objectInstance ?:Permission.ViewChannel

    override suspend fun execute(event: T)
    override suspend fun executePerm(event: Any) {
        event as T
        try {
            val interaction = event.interaction
            val guildId = interaction.data.guildId.value
            if (guildId == null) {
                execute(event)
                return
            }
            val member = interaction.user.asMember(guildId)
            if (member.getPermissions().contains(permission)) execute(event)
            else noPermission(event)
        } catch (e: Error) {
            event.sendMessage("An error occurred while executing this interaction")
            e.printStackTrace()
        }
    }

    override suspend fun noPermission(event: T) {
        event.sendMessage("You don't have permission to use this interaction")
    }
}