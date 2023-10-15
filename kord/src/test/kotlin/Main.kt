import com.azuyamat.Kormmand
import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.map.lruLinkedHashMap
import dev.kord.core.Kord
import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv

lateinit var kord : Kord
val dotenv: Dotenv = Dotenv.load()
val TOKEN: String = dotenv["TOKEN"]?: error("TOKEN not found in .env file")

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    kord = Kord(TOKEN) {
        cache {
            users { cache, description ->
                MapEntryCache(cache, description, MapLikeCollection.concurrentHashMap())
            }

            messages { cache, description ->
                MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(maxSize = 100))
            }

            members { cache, description ->
                MapEntryCache(cache, description, MapLikeCollection.none())
            }
        }
    }

    Kormmand(kord).apply {
        addPackage("interactions")
    }

    kord.login {
        intents {
            +Intents.ALL
        }
        presence {
            playing("with a new bot")
        }
    }
}