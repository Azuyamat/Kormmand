import com.azuyamat.Kormmand
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import io.github.cdimascio.dotenv.Dotenv

val dotenv: Dotenv = Dotenv.load()
val TOKEN: String = dotenv["TOKEN"]?: throw Exception("TOKEN not found in .env")
private lateinit var api: DiscordClient
private lateinit var gatewayDiscordClient: GatewayDiscordClient

fun main() {
    api = DiscordClient.create(TOKEN)
    gatewayDiscordClient = api.login().block() ?: throw Exception("Failed to login.")

    Kormmand(api, gatewayDiscordClient).apply {
        addPackage("interactions")
    }

    gatewayDiscordClient.onDisconnect().block()
}