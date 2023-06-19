package com.kmadsen.adbdesktop.content.logcat

sealed class AdbLogcatLevel(
    val tag: String,
    val value: Int,
) {
    object Error : AdbLogcatLevel("E", 3)
    object Warning : AdbLogcatLevel("W", 4)
    object Info : AdbLogcatLevel("I", 6)
    object Debug : AdbLogcatLevel("D", 7)
    object Verbose : AdbLogcatLevel("V", 8)
    object WhatIsF : AdbLogcatLevel("F", 80)

    companion object {
        fun fromString(logLevel: String): AdbLogcatLevel = when (logLevel) {
            "E" -> Error
            "W" -> Warning
            "I" -> Info
            "D" -> Debug
            "V" -> Verbose
            "F" -> WhatIsF
            else -> throw IllegalStateException("Unknown log level $logLevel")
        }
    }
}
