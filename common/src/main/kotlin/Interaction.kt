package com.azuyamat

import com.azuyamat.utils.firstWord
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

annotation class InteractionClass()

interface Interaction<T : Any> {
    val simpleName : String
        get() = this::class.simpleName?.firstWord() ?: throw Exception("No simple name found")


    suspend fun executePerm(event: Any)
    suspend fun execute(event: T)
    suspend fun noPermission(event: T)
    fun getMainFun() = this::class.memberFunctions.find { it.name == "main" } ?: throw Exception("No main function found")
    fun getOtherFuns() = this::class.memberFunctions.filter { it.name != "main" && it.hasAnnotation<IsSubcommand>() }
    fun getClasses() = this::class.nestedClasses.filter { it.hasAnnotation<IsSubcommand>() }
}