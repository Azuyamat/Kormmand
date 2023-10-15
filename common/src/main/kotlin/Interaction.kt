package com.azuyamat

annotation class InteractionClass()

interface Interaction<T : Any> {
    suspend fun executePerm(event: Any)
    suspend fun execute(event: T)
    suspend fun noPermission(event: T)
}