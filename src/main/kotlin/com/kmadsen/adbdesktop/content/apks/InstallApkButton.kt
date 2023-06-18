package com.kmadsen.adbdesktop.content.apks

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.kmadsen.adbdesktop.adb.AdbDevice
import com.kmadsen.adbdesktop.adb.AdbDevicePoller
import java.io.File

@Composable
fun InstallApkButton(adbDevicePoller: AdbDevicePoller, connectedDevices: List<AdbDevice>, apkFile: File) {
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
