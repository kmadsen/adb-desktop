package com.kmadsen.adbdesktop.drawer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class Terminal {
    private val runtime: Runtime = Runtime.getRuntime()

    suspend fun run(command: String): List<String> = withContext(Dispatchers.IO) {
        println(
            """EXECUTE ADB
                    $command
                """.trimIndent()
        )
        val process = runtime.exec(command)
        fun InputStream.readLines() = bufferedReader()
            .readLines()
            .map { it.trim() }

        val errors = process.errorStream.readLines()
        return@withContext if (errors.isNotEmpty()) {
            println("$command failed: $errors")
            emptyList()
        } else {
            process.inputStream.readLines()
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
