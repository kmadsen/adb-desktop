package com.adb.desktop

import java.io.InputStream

class Terminal {
    private val runtime = Runtime.getRuntime()

    fun run(command: String): List<String> {
        val process = runtime.exec(command)

        fun InputStream.readLines() = bufferedReader()
            .readLines()
            .map { it.trim() }

        val errors = process.errorStream.readLines()
        if (errors.isNotEmpty()) {
            println("$command failed: $errors")
            return emptyList()
        }

        return process.inputStream.readLines()
    }
}
