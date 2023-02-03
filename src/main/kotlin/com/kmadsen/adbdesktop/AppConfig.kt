package com.kmadsen.adbdesktop

import androidx.compose.ui.res.useResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class AppConfigValues(
    val apkDirectory: String? = null
)

object AppConfig {

    private const val DEFAULT_FILE_PATH = "app_config.json"

    val configFlow = MutableStateFlow(AppConfigValues())
    private val appResourcePath by lazy { loadResourcePath() }

    fun ensureConfigExists() {
        val configFile = File(appResourcePath, DEFAULT_FILE_PATH)
        val createdNewFile = configFile.createNewFile()
        if (createdNewFile) {
            configFile.outputStream().use { fos ->
                val json = Json.encodeToString(AppConfigValues())
                fos.write(json.toByteArray())
            }
        } else {
            configFlow.value = load(configFile.readText())
        }
    }

    suspend fun update(function: (AppConfigValues) -> AppConfigValues) = withContext(Dispatchers.IO) {
        configFlow.update {
            val nextValue = function(it)
            File(appResourcePath, DEFAULT_FILE_PATH).outputStream().use { fos ->
                val json = Json.encodeToString(nextValue)
                fos.write(json.toByteArray())
            }
            nextValue
        }
    }

    private fun loadResourcePath(): String {
        val path = javaClass.getResource("/")?.toURI()?.path
        checkNotNull(path) { "Could not find project resources" }
        println("appResourcePath: $path")
        return path
    }

    private fun load(json: String): AppConfigValues {
        val configValues: AppConfigValues = Json.decodeFromString(AppConfigValues.serializer(), json)
        configFlow.value = configValues
        return configValues
    }
}
