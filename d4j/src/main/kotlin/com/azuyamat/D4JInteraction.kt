package com.azuyamat

import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.rest.util.Permission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.full.findAnnotation

annotation class RequirePermission(val permission: Permission)

interface D4JInteraction<T: InteractionCreateEvent> : Interaction<T> {
    val permission: Permission get() = this::class.findAnnotation<RequirePermission>()?.permission?:Permission.VIEW_CHANNEL

    override suspend fun execute(event: T)
    override suspend fun executePerm(event: Any) {
        event as T
        try {
            if (withContext(Dispatchers.IO) {
                    event.interaction.member.get().basePermissions.block()
                }?.contains(permission) == true) execute(event)
            else noPermission(event)
        } catch (e: Exception) {
            event.sendMessage("An error occurred while executing this interaction")
            e.printStackTrace()
        }
    }

    override suspend fun noPermission(event: T) {
        event.sendMessage("You don't have permission to use this interaction")
    }

}