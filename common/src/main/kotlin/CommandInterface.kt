package com.azuyamat

import kotlin.reflect.full.findAnnotation

interface CommandInterface<T: Any> : Interaction<T> {
    val description: String
        get() = this::class.findAnnotation<CommandClass>()?.description ?: throw Exception("No description found")
}