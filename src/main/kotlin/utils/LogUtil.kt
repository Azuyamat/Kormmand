package utils

fun log(message: String) {
    println("${cyan("[COMMAND MANAGER]")} $message")
}

fun cyan(message: String): String {
    return "\u001B[36m$message\u001B[0m"
}