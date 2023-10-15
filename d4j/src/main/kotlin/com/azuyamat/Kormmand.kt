package com.azuyamat

import com.azuyamat.utils.LogUtil.cyan
import com.azuyamat.utils.firstWord
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.InteractionCreateEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

class Kormmand(private val bot: DiscordClient, private val gateway: GatewayDiscordClient) : Manager {
    private val applicationId  = bot.applicationId.block()!!

    override val interactions: MutableMap<Any, MutableMap<String, Interaction<*>>> = mutableMapOf()
    override val guildInteractions: MutableMap<Any, MutableMap<String, MutableMap<String, Interaction<*>>>> = mutableMapOf()
    override var debug: Boolean = true
    override var packages: List<Package> = emptyList()

    init {
        gateway.on(ReadyEvent::class.java).subscribe {
            logMessage("Using Discord4J")
            if (packages.isEmpty()) logMessage("No packages are set to be registered. If this is an error, please add packages using addPackage(<name>, [guildId])")
            else logMessage("Ready to register ${cyan(packages.size.toString())} package(s)")
            runBlocking {
                for (pkg in packages) {
                    registerInteractions(pkg.name, pkg.guildId)
                }
            }
        }

        gateway.on(InteractionCreateEvent::class.java).subscribe {
            runBlocking {
                handleInteraction(it)
            }
        }
    }

    override suspend fun registerCommands() {
        val globalC = interactions["Command"]?.values?.mapNotNull { it ->
             it as D4JInteraction<*>
             val commandName = it::class.simpleName?.firstWord() ?: throw Exception("Command name not found")
             val commandDescription = it::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Command description not found")

             val builder: ImmutableApplicationCommandRequest.Builder = ApplicationCommandRequest.builder()
                 .name(commandName)
                 .description(commandDescription)
                 .defaultMemberPermissions(it.permission.value.toString())
             var functions = it::class.memberFunctions
             val mainFun = functions.find { it.name == "main" } ?: return@mapNotNull builder
             functions = functions.filter { it.name != "main" }.filter { it.hasAnnotation<IsSubcommand>() }

             //Main options
             builder.addAllOptions(
                 mainFun.valueParameters.mapNotNull Foo@ { param ->
                     val paramClass = param.type.classifier?: throw Exception("Parameter class not found for ${param.name} in ${it::class.simpleName}")
                     val option = getOptionTypeClass(paramClass) ?: return@Foo null

                     ApplicationCommandOptionData.builder()
                         .name(param.name ?: "")
                         .description(param.findAnnotation<Description>()?.description ?: "${param.name} parameter")
                         .required(!param.type.isMarkedNullable)
                         .type(option.value)
                         .build()
                 }
             )

             //Subcommands
             builder.addAllOptions(
                 functions.map { subC ->
                     val subCommandName = subC::class.simpleName?.firstWord() ?: throw Exception("Subcommand name not found")
                     val subCommandDescription = subC::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Subcommand description not found")

                     ApplicationCommandOptionData.builder()
                         .name(subCommandName)
                         .description(subCommandDescription)
                         .type(ApplicationCommandOption.Type.SUB_COMMAND.value)
                         .addAllOptions(
                             subC.valueParameters.map { subParam ->
                                 val paramClass = subParam.type.classifier?: throw Exception("Parameter class not found for ${subParam.name} in ${subParam::class.simpleName}")
                                 val option = getOptionTypeClass(paramClass)
                                 ApplicationCommandOptionData.builder()
                                     .name(subParam.name ?: "")
                                     .description(subParam.findAnnotation<Description>()?.description ?: "${subParam.name} parameter")
                                     .required(!subParam.type.isMarkedNullable)
                                     .type(option?.value?:0)
                                     .build()
                             }
                         )
                         .build()
                 }
             )
         }!!

        /**
         * Register guild commands
         */
        val guildC: MutableMap<String, List<ImmutableApplicationCommandRequest.Builder>> = mutableMapOf()
        guildInteractions["Command"]?.forEach { (guildId, commands) ->
            val builderList = commands.values.map { cmd ->
                val commandName = cmd::class.simpleName?.firstWord() ?: throw Exception("Command name not found")
                val commandDescription = cmd::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Command description not found")

                val builder: ImmutableApplicationCommandRequest.Builder = ApplicationCommandRequest.builder()
                    .name(commandName)
                    .description(commandDescription)
                var functions = cmd::class.memberFunctions
                val mainFun = functions.find { it.name == "main" } ?: return@map builder
                functions = functions.filter { it.name != "main" }.filter { it.hasAnnotation<IsSubcommand>() }
                //Main options
                builder.addAllOptions(
                    mainFun.valueParameters.mapNotNull { param ->

                        val paramClass = param.type.classifier?: throw Exception("Parameter class not found for ${param.name} in ${cmd::class.simpleName}")
                        val option = getOptionTypeClass(paramClass) ?: return@mapNotNull null

                        ApplicationCommandOptionData.builder()
                            .name(param.name ?: "")
                            .description(param.findAnnotation<Description>()?.description ?: "${param.name} parameter")
                            .required(!param.type.isMarkedNullable)
                            .type(option.value)
                            .build()
                    }.filter {p -> p.type() != 0 }
                )

                //Subcommands
                builder.addAllOptions(
                    functions.map { subC ->
                        val subCommandName = subC::class.simpleName?.firstWord() ?: throw Exception("Subcommand name not found")
                        val subCommandDescription = subC::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Subcommand description not found")

                        ApplicationCommandOptionData.builder()
                            .name(subCommandName)
                            .description(subCommandDescription)
                            .type(ApplicationCommandOption.Type.SUB_COMMAND.value)
                            .addAllOptions(
                                subC.valueParameters.mapNotNull { subParam ->
                                    val paramClass = subParam.type.classifier?: throw Exception("Parameter class not found for ${subParam.name} in ${subParam::class.simpleName}")
                                    val option = getOptionTypeClass(paramClass) ?: return@mapNotNull null
                                    ApplicationCommandOptionData.builder()
                                        .name(subParam.name ?: "")
                                        .description(subParam.findAnnotation<Description>()?.description ?: "${subParam.name} parameter")
                                        .required(!subParam.type.isMarkedNullable)
                                        .type(option.value)
                                        .build()
                                }
                            )
                            .build()
                    }
                )
            }
            guildC[guildId] = builderList
        }

        withContext(Dispatchers.IO) {
            try {
                globalC.forEach { gc ->
                    gc.build().let { bot.applicationService.createGlobalApplicationCommand(applicationId, it) }.doOnNext { cmd ->
                        logMessage("${cyan("(SUCCESS)")} Registered global command ${cyan(cmd.name())}")
                    }.block()
                }
                guildC.map { (guildId, commands) ->
                    commands.forEach { cmd ->
                        bot.applicationService.createGuildApplicationCommand(
                            applicationId,
                            guildId.toLong(),
                            cmd.build()
                        ).block()
                    }
                }
            } catch (e: Exception) {
                logMessage("${cyan("(ERROR)")} ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override suspend fun handleInteraction(event: Any) {
        event as InteractionCreateEvent
        val id: String = event.getTypedId()?.split("//")?.get(0) ?: return
        val guildId = event.interaction.guildId.get().asString()
        executeInteraction(id, event, guildId)
    }

    override suspend fun executeInteraction(id: String, event: Any, guildId: String?) {
        event as InteractionCreateEvent
        val eventType = event.getClassType() ?: return
        logMessage("Executing $eventType $id")
        val interaction = interactions[eventType]?.get(id)
        if (interaction == null) {
            logMessage("${cyan("(ERROR)")} Interaction $id not found")
            return
        }
        interaction.executePerm(event)
    }
}