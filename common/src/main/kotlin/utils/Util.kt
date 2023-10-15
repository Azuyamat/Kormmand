package com.azuyamat.utils

fun String.firstWord() = Regex("^([A-Z]|[a-z])[a-z]+").find(this)?.value?.lowercase()
/**
 * First word of a string
 * Ex: JeffCommand -> jeff
 * Ex: jeffCommand -> jeff
 */