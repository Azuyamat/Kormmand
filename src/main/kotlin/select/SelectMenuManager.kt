package select

import Manager
import button.Button
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on

class SelectMenuManager(kord: Kord) : Manager<SelectMenu, SelectMenuInteractionCreateEvent> {
    override val name = "SelectMenuManager"
    override val bot: Kord = kord
    override val interactions: MutableMap<String, SelectMenu> = mutableMapOf()
    override val guildInteractions: MutableMap<String, MutableMap<String, SelectMenu>> = mutableMapOf()

    init {
        bot.on<SelectMenuInteractionCreateEvent> {
            handleInteraction(this)
        }
    }
}