package com.azuyamat

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import kotlin.reflect.full.findAnnotation

annotation class RequirePermission(val permission: Permission)

interface JDAInteraction<T: GenericInteractionCreateEvent> : Interaction<T> {
    val permission: Permission get() = this::class.findAnnotation<RequirePermission>()?.permission?:Permission.VIEW_CHANNEL

    override suspend fun execute(event: T)
    override suspend fun executePerm(event: Any) {
        event as T
        try {
            if (event.member?.hasPermission(permission) == true) execute(event)
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