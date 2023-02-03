package com.kmadsen.adbdesktop.apk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.kmadsen.adbdesktop.AppConfig
import com.kmadsen.adbdesktop.drawer.AdbDevice
import com.kmadsen.adbdesktop.drawer.AdbDevicePoller
import com.kmadsen.adbdesktop.drawer.rememberAdbDeviceManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

object ApkUi {
    @Composable
    fun buildDirectorySelect() {
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
                        buildApkCard(apkFile)
                    }
            }
        }
    }

    /**
     * Builds a selectable apk card
     */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun buildApkCard(apkFile: File) {
        // TODO share the adbDevicePoller between all the apk cards
        val adbDevicePoller = rememberAdbDeviceManager()
        var connectedDevices by remember { mutableStateOf(emptyList<AdbDevice>()) }
        adbDevicePoller.poll { freshDevices -> connectedDevices = freshDevices }
        var selected by remember { mutableStateOf(false) }

        Card(
            elevation = 4.dp,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            border = if (selected) BorderStroke(2.dp, Color.Blue) else null,
            onClick = { selected = !selected }
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .heightIn(50.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Is apk file: ${apkFile.name}")
                Spacer(modifier = Modifier.weight(1f))
                if (selected) {
                    buildInstallApkButton(adbDevicePoller, connectedDevices, apkFile)
                }
            }
        }
    }

    @Composable
    fun buildInstallApkButton(adbDevicePoller: AdbDevicePoller, connectedDevices: List<AdbDevice>, apkFile: File) {
        if (connectedDevices.isNotEmpty()) {
            connectedDevices.forEach {
                Button(
                    onClick = { adbDevicePoller.installAndRunApk(it, apkFile) }
                ) {
                    Text("Run on ${it.deviceId}")
                }
            }
        } else {
            Text("No device connected")
        }
    }
}
