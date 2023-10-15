package com.azuyamat

import com.azuyamat.utils.LogUtil.cyan
import com.azuyamat.utils.firstWord
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import kotlin.reflect.full.*

class Kormmand(private val bot: JDA) : Manager, ListenerAdapter() {
    override val interactions: MutableMap<Any, MutableMap<String, Interaction<*>>> = mutableMapOf()
    override val guildInteractions: MutableMap<Any, MutableMap<String, MutableMap<String, Interaction<*>>>> = mutableMapOf()
    override var debug: Boolean = true
    override var packages: List<Package> = emptyList()

    init {
        bot.addEventListener(this)
    }

    override fun onReady(event: ReadyEvent) {
        logMessage("Using JDA")
        if (packages.isEmpty()) logMessage("No packages are set to be registered. If this is an error, please add packages using addPackage(<name>, [guildId])")
        else logMessage("Ready to register ${cyan(packages.size.toString())} package(s)")
        runBlocking {
            for (pkg in packages) {
                registerInteractions(pkg.name, pkg.guildId)
            }
        }
    }
    override fun onGenericInteractionCreate(event: GenericInteractionCreateEvent) {
        runBlocking {
            handleInteraction(event)
        }
    }

    override suspend fun registerCommands() {

        logMessage("Registering commands")
        val globalC: MutableCollection<CommandData> = mutableListOf()

        /**
         * Register global commands
         */
        interactions["Command"]?.values?.map { it ->
            it as JDAInteraction<*>
            val commandName = it::class.simpleName?.firstWord() ?: throw Exception("Command name not found")
            val commandDescription = it::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Command description not found")

            val builder = Commands.slash(commandName, commandDescription)
            var functions = it::class.memberFunctions
            val mainFun = functions.find { it.name == "main" } ?: return@map builder
            functions = functions.filter { it.name != "main" }.filter { it.hasAnnotation<IsSubcommand>() }

            mainFun.valueParameters.forEach { param ->
                val paramClass = param.type.classifier
                val option = getOptionTypeClass(paramClass)
                if (option != null)
                    builder.addOption(
                        option,
                        param.name ?: "",
                        param.findAnnotation<Description>()?.description ?: "${param.name} parameter",
                        !param.type.isMarkedNullable
                    )
            }

            val subCommands: MutableCollection<SubcommandData> = mutableListOf()
            functions.forEach { subC ->
                val subCommandName = subC.name.firstWord()?: throw Exception("Subcommand name not found")
                val subCommandDescription = subC.findAnnotation<IsSubcommand>()?.description?: throw Exception("Subcommand description not found")
                val subCommandData = SubcommandData(subCommandName, subCommandDescription)
                subC.valueParameters.forEach { param ->
                    val paramClass = param.type.classifier
                    val option = getOptionTypeClass(paramClass)
                    if (option != null)
                        subCommandData.addOption(
                            option,
                            param.name ?: "",
                            param.findAnnotation<Description>()?.description ?: "${param.name} parameter",
                            !param.type.isMarkedNullable
                        )
                }
                subCommands.add(subCommandData)
            }

            builder.addSubcommands(subCommands)
            builder.setDefaultPermissions(DefaultMemberPermissions.enabledFor(it.permission))

            builder
        }?.toCollection(globalC)
        bot.updateCommands().addCommands(globalC).queue {
            logMessage("Registered ${cyan(it.size.toString())} command(s) to Discord's API")
        }

        /**
         * Register guild commands
         */
        guildInteractions["Command"]?.forEach { (guildId, commands) ->
            val guildCommands: MutableCollection<CommandData> = mutableListOf()
            commands.values.map { it ->
                it as JDAInteraction<*>
                val commandName = it::class.simpleName?.firstWord() ?: throw Exception("Command name not found")
                val commandDescription = it::class.findAnnotation<CommandClass>()?.description ?: throw Exception("Command description not found")

                val builder = Commands.slash(commandName, commandDescription)
                var functions = it::class.memberFunctions
                val mainFun = functions.find { it.name == "main" } ?: return@map builder
                functions = functions.filter { it.name != "main" }.filter { it.hasAnnotation<IsSubcommand>() }

                mainFun.valueParameters.forEach { param ->
                    val paramClass = param.type.classifier
                    val option = getOptionTypeClass(paramClass)
                    if (option != null)
                        builder.addOption(
                            option,
                            param.name ?: "",
                            param.findAnnotation<Description>()?.description ?: "${param.name} parameter",
                            !param.type.isMarkedNullable
                        )
                }

                val subCommands: MutableCollection<SubcommandData> = mutableListOf()
                functions.forEach { subC ->
                    val subCommandName = subC.name.firstWord() ?: throw Exception("Subcommand name not found")
                    val subCommandDescription = subC.findAnnotation<IsSubcommand>()?.description ?: throw Exception("Subcommand description not found")
                    subCommands.add(SubcommandData(subCommandName, subCommandDescription))
                }

                builder.addSubcommands(subCommands)
                builder.setDefaultPermissions(DefaultMemberPermissions.enabledFor(it.permission))

                builder
            }.toCollection(guildCommands)
            bot.getGuildById(guildId)?.updateCommands()?.addCommands(guildCommands)?.queue {
                logMessage("Registered ${cyan(it.size.toString())} guild command(s) to Discord's API for guild ${cyan(guildId)}")
            }
        }
    }

    override suspend fun handleInteraction(event: Any) {
        event as GenericInteractionCreateEvent
        val id: String = event.getTypedId()?.split("//")?.get(0) ?: return
        val guildId = event.guild?.id ?: return
        executeInteraction(id, event, guildId)
    }

    override suspend fun executeInteraction(id: String, event: Any, guildId: String?) {
        event as GenericInteractionCreateEvent
        val eventType = event.getClassType() ?: return
        logMessage(eventType)
        val interaction = interactions[eventType]?.get(id)
        if (interaction != null) executePerm(interaction, event)
        else {
            val guildCommand = guildInteractions[eventType]?.get(guildId)?.get(id)
            if (guildCommand != null) executePerm(guildCommand, event)
            else {
                event.sendMessage("Interaction not found with id $id")
            }
        }
    }
}