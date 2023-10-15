package com.azuyamat

import com.azuyamat.utils.LogUtil.cyan
import com.azuyamat.utils.firstWord
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder
import kotlinx.coroutines.runBlocking
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

class Kormmand(private val bot: Kord) : Manager {
    override val interactions: MutableMap<Any, MutableMap<String, Interaction<*>>> = mutableMapOf()
    override val guildInteractions: MutableMap<Any, MutableMap<String, MutableMap<String, Interaction<*>>>> = mutableMapOf()
    override var debug: Boolean = true
    override var packages: List<Package> = emptyList()

    init {
        runBlocking {
            bot.on<InteractionCreateEvent> { handleInteraction(this) }
            bot.on<ReadyEvent> {
                logMessage("Using Kord")
                if (packages.isEmpty()) logMessage("No packages are set to be registered. If this is an error, please add packages using addPackage(<name>, [guildId])")
                else logMessage("Ready to register ${cyan(packages.size.toString())} package(s)")
                for (pkg in packages){
                    registerInteractions(pkg.name, pkg.guildId)
                }
            }
        }
    }

    override suspend fun registerCommands() {

        /**
         * Register global commands
         */
        bot.createGlobalApplicationCommands {
            interactions["Command"]?.values?.map {
                it as KordInteraction<*>
                val commandName = it::class.simpleName?.firstWord() ?: throw Exception("Command name not found")
                val commandDescription = it::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Command description not found")
                var functions = it::class.memberFunctions
                lateinit var builder: GlobalChatInputCreateBuilder
                input(commandName, commandDescription){
                    builder = this
                }
                val mainFun = functions.find { curFun ->  curFun.name == "main" } ?: return@map builder
                functions = functions.filter { curFun -> curFun.name != "main" }.filter { curFun -> curFun.hasAnnotation<IsSubcommand>() }

                mainFun.valueParameters.forEach { param ->
                    val paramClass = param.type.classifier ?: return
                    builder.addOption(paramClass, param.name ?: "", param.name ?: "")
                }

                functions.forEach { curFun ->
                    val subcommandName = curFun.name
                    val subcommandDescription = curFun.findAnnotation<IsSubcommand>()?.description ?: throw Exception("Subcommand description not found")

                    builder.addSubcommand(subcommandName, subcommandDescription){
                        curFun.valueParameters.forEach { param ->
                            val paramClass = param.type.classifier ?: return@addSubcommand
                            addOption(paramClass, param.name ?: "", param.name ?: "")
                        }
                    }
                }

                builder.defaultMemberPermissions = Permissions(it.permission)
                builder
            }
        }.apply {
            collect {
                logMessage("Registered ${cyan(it.name)} as a global command")
            }
        }

        /**
         * Register guild commands
         */
        guildInteractions["Command"]?.forEach { (guildId, commands) ->
            bot.createGuildApplicationCommands(Snowflake(guildId)){
                commands.values.forEach{ command ->
                    command as KordInteraction<*>
                    val commandName = command::class.simpleName?.firstWord() ?: throw Exception("Command name not found")
                    val commandDescription = command::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Command description not found")
                    var functions = command::class.memberFunctions
                    lateinit var builder: ChatInputCreateBuilder
                    input(commandName, commandDescription) {
                        builder = this
                    }
                    val mainFun = functions.find { curFun ->  curFun.name == "main" } ?: return@forEach
                    functions = functions.filter { curFun -> curFun.name != "main" }.filter { curFun -> curFun.hasAnnotation<IsSubcommand>() }

                    mainFun.valueParameters.forEach { param ->
                        val paramClass = param.type.classifier ?: return
                        builder.addOption(paramClass, param.name ?: "", param.name ?: "")
                    }

                    functions.forEach { curFun ->
                        val subcommandName = curFun.name
                        val subcommandDescription = curFun.findAnnotation<IsSubcommand>()?.description ?: throw Exception("Subcommand description not found")

                        builder.addSubcommand(subcommandName, subcommandDescription){
                            curFun.valueParameters.forEach { param ->
                                val paramClass = param.type.classifier ?: return@addSubcommand
                                addOption(paramClass, param.name ?: "", param.name ?: "")
                            }
                        }
                    }

                    builder.defaultMemberPermissions = Permissions(command.permission)
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
}
