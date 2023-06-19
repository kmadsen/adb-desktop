package com.kmadsen.adbdesktop.content.logcat

data class AdbLogEntry(
    val date: String,
    val time: String,
    val processId: Int,
    val threadId: Int,
    val logLevel: AdbLogcatLevel,
    val tag: String,
    val message: String
)
