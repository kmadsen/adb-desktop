package com.kmadsen.adbdesktop.drawer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class Terminal {
    suspend fun run(command: String): List<String> = withContext(Dispatchers.IO) {
        if (command.isEmpty()) {
            println("Command is null or empty.")
            return@withContext emptyList()
        }

        println(
            """EXECUTE ADB
            $command
        """.trimIndent()
        )

        val process = ProcessBuilder(*command.split(" ").toTypedArray())
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readLines()
        val errors = process.errorStream.bufferedReader().readLines()

        if (errors.isNotEmpty()) {
            println("$command failed: $errors")
            emptyList()
        } else {
            output
        }.also {
            println(
                """COMPLETED EXECUTE ADB
                $command
                $it
            """.trimIndent()
            )
        }
    }
}
