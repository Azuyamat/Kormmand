package com.azuyamat

import com.azuyamat.interactions.Command
import com.azuyamat.utils.LogUtil.cyan
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

class Kormmand(private val bot: Kord) : Manager {
    override val interactions: MutableMap<Any, MutableMap<String, Interaction<*>>> = mutableMapOf()
    override val guildInteractions: MutableMap<Any, MutableMap<String, MutableMap<String, Interaction<*>>>> = mutableMapOf()
    override var debug: Boolean = true
    override var packages: List<Package> = emptyList()

    init {
        runBlocking {
            bot.on<InteractionCreateEvent> { handleInteraction(this) }
            bot.on<ReadyEvent> { registerPackages() }
        }
    }

    override suspend fun registerCommands() {

        /**
         * Register global commands
         */
        try {
            bot.createGlobalApplicationCommands {
                interactions["Command"]?.values?.map {
                    (it as Command).addToBuilder(this)
                }
            }.onEach {
                logMessage("Registered ${cyan(it.name)} as a global command")
            }
        } catch (e: Exception) {
            logMessage("Failed to register global commands")
            e.printStackTrace()
        }

        /**
         * Register guild commands
         */
        guildInteractions["Command"]?.forEach { (guildId, commands) ->
            bot.createGuildApplicationCommands(Snowflake(guildId)) {
                commands.values.forEach {
                    (it as Command).addToBuilder(this@createGuildApplicationCommands)
                }
            }
        }
    }

    override suspend fun handleInteraction(event: Any) {
        event as InteractionCreateEvent
        val talk = event.interaction
        var id = talk.data.data.customId.value ?: talk.data.data.name.value ?: return
        id = id.split("//")[0]
        val channel = talk.getChannelOrNull() as TextChannel?
        val guildId = channel?.guildId?.toString()
        executeInteraction(id, event, guildId)
    }

    override suspend fun executeInteraction(id: String, event: Any, guildId: String?) {
        event as InteractionCreateEvent
        val eventType = event.getClassType() ?: return
        val interaction = interactions[eventType]?.get(id)
        if (interaction != null) executePerm(interaction, event)
        else {
            val guildCommand = guildInteractions[eventType]?.get(guildId)?.get(id)
            if (guildCommand != null) executePerm(guildCommand, event)
            else event.sendMessage("This interaction doesn't exist")
        }
    }

    override suspend fun sendMessage(event: Any, message: String) {
        TODO("Not yet implemented")
    }
}
