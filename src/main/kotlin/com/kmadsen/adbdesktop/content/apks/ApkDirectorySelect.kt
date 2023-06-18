package com.kmadsen.adbdesktop.content.apks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.kmadsen.adbdesktop.AppConfig
import com.kmadsen.adbdesktop.AppContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ApkDirectorySelect(appContext: AppContext) {
    val coroutineScope = rememberCoroutineScope()
    val apkDirectory by AppConfig.configFlow.map { it.apkDirectory }.collectAsState(initial = null)
    var showDirPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.width(500.dp)
    ) {
        Row {
            Button(onClick = { showDirPicker = true }) {
                Text("Change apk directory")
            }
            Text(
                text = apkDirectory ?: "No directory selected",
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            )
            DirectoryPicker(
                show = showDirPicker,
                initialDirectory = apkDirectory,
            ) { path ->
                showDirPicker = false
                path?.let {
                    coroutineScope.launch { AppConfig.update { it.copy(apkDirectory = path) } }
                }
            }
        }
        apkDirectory?.let { value ->
            File(value).walk()
                .filter { file ->
                    file.isFile && file.extension == "apk" && !file.name.startsWith(".")
                }
                .forEach { apkFile ->
                    ApkCard(appContext, apkFile)
                }
        }
    }
}
