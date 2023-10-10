package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val RESET = "\u001B[0m"
private const val CYAN = "\u001B"
private const val GREEN = "\u001B[32m"

fun log(message: String) {
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS")
    val formattedTime = currentTime.format(formatter)
    println(cyan(" [${formattedTime}] ") + message)
}
fun cyan(message: String) : String = "$CYAN[36m$message$RESET"