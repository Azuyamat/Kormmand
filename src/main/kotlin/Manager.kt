import utils.log
import utils.cyan

interface Manager {
    val name : String

    fun logMessage(message : String) = log(cyan("[${name.uppercase()}]") + message)
}