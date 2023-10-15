import com.azuyamat.Kormmand
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

val dotenv: Dotenv = Dotenv.load()
val TOKEN: String = dotenv["TOKEN"]?: throw Exception("TOKEN not found in .env")
private lateinit var api: JDA

fun main() {
    api = JDABuilder.createDefault(TOKEN)
        .enableIntents(GatewayIntent.GUILD_MEMBERS)
        .build()
    Kormmand(api).apply {
        addPackage("interactions")
    }
}