package com.kmadsen.adbdesktop.content.apks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmadsen.adbdesktop.AppContext
import com.kmadsen.adbdesktop.adb.AdbDevice
import com.kmadsen.adbdesktop.adb.AdbDevicePoller
import java.io.File

@Composable
fun rememberAdbDeviceManager(appContext: AppContext): AdbDevicePoller {
    val coroutineScope = rememberCoroutineScope()
    return AdbDevicePoller(appContext.adb, coroutineScope)
}

/**
 * Builds a selectable apk card
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ApkCard(appContext: AppContext, apkFile: File) {
    // TODO share the adbDevicePoller between all the apk cards
    val adbDevicePoller = rememberAdbDeviceManager(appContext)
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
                InstallApkButton(adbDevicePoller, connectedDevices, apkFile)
            }
        }
    }
}
