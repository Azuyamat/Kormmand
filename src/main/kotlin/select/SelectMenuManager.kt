package select

import Manager
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on

class SelectMenuManager(kord: Kord) : Manager {
    override val name = "SelectMenuManager"

    private val bot = kord
    private val selectMenus: MutableMap<String, SelectMenu> = mutableMapOf()


    init {
        bot.on<SelectMenuInteractionCreateEvent> {
            handleInteraction(this)
        }
    }

    fun registerSelectMenus(selectMenus : List<SelectMenu>) {
        val startTime = System.currentTimeMillis()

        for (selectMenu in selectMenus) {
            registerSelectMenu(selectMenu)
        }

        val endTime = System.currentTimeMillis()
        val elapsedTime = endTime - startTime

        logMessage("Registered ${selectMenus.size} select menus (${elapsedTime}ms)")
    }

    private fun registerSelectMenu(selectMenu: SelectMenu){
        registerCommandToList(selectMenu.id, selectMenu)
        logMessage("Registered select menu: ${selectMenu.name ?: selectMenu.id}")
    }

    fun getSelectMenus() : MutableMap<String, SelectMenu> {
        return selectMenus
    }


    private fun registerCommandToList(id: String, selectMenu: SelectMenu) {
        selectMenus[id] = selectMenu
    }

    private suspend fun handleInteraction(event: SelectMenuInteractionCreateEvent) {
        val interaction = event.interaction
        val id = interaction.componentId.split("//")[0]

        val modal = selectMenus[id]
        if (modal != null) modal.executePerm(event)
        else interaction.respondEphemeral { content = "Button not found" }
    }
}