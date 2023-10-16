package com.azuyamat

import com.azuyamat.utils.LogUtil.cyan
import com.azuyamat.utils.LogUtil.log
import com.azuyamat.utils.firstWord
import org.reflections.Reflections
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

interface Manager {
    val name: String
        get() = this::class.simpleName?:"Manager"
    val interactions: MutableMap<Any, MutableMap<String, Interaction<*>>>
    val guildInteractions: MutableMap<Any, MutableMap<String, MutableMap<String, Interaction<*>>>>
    var debug: Boolean
    var packages: List<Package>

    val globalCommands get() = interactions["Command"] ?: mutableMapOf()
    val globalButtons get() = interactions["Button"] ?: mutableMapOf()
    val globalModals get() = interactions["Modal"] ?: mutableMapOf()
    val globalSelectMenus get() = interactions["SelectMenu"] ?: mutableMapOf()

    val guildCommands get() = guildInteractions["Command"] ?: mutableMapOf()
    val guildButtons get() = guildInteractions["Button"] ?: mutableMapOf()
    val guildModals get() = guildInteractions["Modal"] ?: mutableMapOf()
    val guildSelectMenus get() = guildInteractions["SelectMenu"] ?: mutableMapOf()

    suspend fun registerPackages(){
        logMessage("Using ${cyan(name)}")
        if (packages.isEmpty()) logMessage("No packages are set to be registered. If this is an error, please add packages using addPackage(<name>, [guildId])")
        else logMessage("Ready to register ${cyan(packages.size.toString())} package(s)")
        for (pkg in packages){
            registerInteractions(pkg.name, pkg.guildId)
        }
    }
    suspend fun registerInteractions(prefix: String, guildId: String? = null) {
        val registryType = if (guildId == null) "Global" else "Guild"
        val startTime = System.currentTimeMillis()

        logMessage("($registryType) Registering interactions in ${cyan(prefix)}")

        val reflections: MutableSet<Class<*>> = Reflections(prefix).getTypesAnnotatedWith(CommandClass::class.java).toMutableSet()
        reflections += Reflections(prefix).getTypesAnnotatedWith(InteractionClass::class.java)

        logMessage("↳ Found ${cyan(reflections.size.toString())} interaction(s)")

        reflections.map { it.kotlin }.forEach { interaction ->
            val instance = interaction.objectInstance as Interaction<*>
            registerInteraction(instance, guildId)
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        val amt: Int = if (guildId == null) interactions.map { it.value.size }.sum() else guildInteractions.map { it.value.map { it1 -> it1.value.size }.sum() }.sum()
        logMessage("$registryType Registered $amt (${elapsedTime}ms)")
        registerCommands()
    }
    suspend fun registerCommands()
    private fun registerInteraction(interaction: Interaction<*>, guildId: String? = null) {
        val identifier: String = interaction::class.simpleName?.firstWord()?: throw Exception("Interaction name not found")
        val simpleName = interaction::class.java.interfaces.first().simpleName
        val interactionGuildId =
            if (interaction::class.hasAnnotation<GuildOnly>()) interaction::class.findAnnotation<GuildOnly>()?.guildId ?: guildId
            else guildId
        if (interactionGuildId == null)
            interactions.getOrPut(simpleName) { mutableMapOf() }[identifier] = interaction
        else
            guildInteractions.getOrPut(simpleName) { mutableMapOf() }.getOrPut(interactionGuildId) { mutableMapOf() }[identifier] = interaction
        logMessage("↳ (${cyan(simpleName.uppercase())}) Registered ${cyan(identifier)}${cyan(if (interactionGuildId != null) " to guild $interactionGuildId " else "")} from ${cyan(interaction::class.qualifiedName?:"Unknown")}")
    }
    suspend fun handleInteraction(event: Any)
    suspend fun executeInteraction(id: String, event: Any, guildId: String? = null)
    suspend fun executePerm(interaction: Interaction<*>, event: Any) = interaction.executePerm(event)
    suspend fun sendMessage(event: Any, message: String) {}

    fun logMessage(message: String) = if (debug) log(cyan("[${name.uppercase()}] ") + message) else null
    fun disableDebug() {
        debug = false
    }

    fun addPackage(name: String, guildId: String? = null) {
        packages += Package(name, guildId)
    }
}