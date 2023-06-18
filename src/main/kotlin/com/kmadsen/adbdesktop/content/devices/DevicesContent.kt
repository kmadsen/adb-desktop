package com.kmadsen.adbdesktop.content.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmadsen.adbdesktop.AppContext
import com.kmadsen.adbdesktop.content.apks.ApkDirectorySelect
import com.kmadsen.adbdesktop.adb.AdbDevice
import com.kmadsen.adbdesktop.adb.AdbDevicePoller
import com.kmadsen.adbdesktop.adb.AndroidVirtualDevice

@Composable
fun rememberAdbDeviceManager(appContext: AppContext): AdbDevicePoller {
    val coroutineScope = rememberCoroutineScope()
    return AdbDevicePoller(appContext.adb, coroutineScope)
}

@Composable
fun DevicesContent(appContext: AppContext) {
    val adbDevicePoller = rememberAdbDeviceManager(appContext)

    var connectedDevices by remember { mutableStateOf(emptyList<AdbDevice>()) }
    adbDevicePoller.poll { freshDevices -> connectedDevices = freshDevices }
    var androidVirtualDevices by remember { mutableStateOf(emptyList<AndroidVirtualDevice>()) }
    adbDevicePoller.request { freshAvds -> androidVirtualDevices = freshAvds }

    Row {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Connected devices",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            connectedDevices.ifEmpty {
                EmptyDeviceCard()
                null
            }?.forEach { deviceId ->
                AdbDeviceCard(adbDevicePoller, deviceId)
            }
            ApkDirectorySelect(appContext)
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Android virtual devices (AVDs)",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            androidVirtualDevices.ifEmpty {
                EmptyDeviceCard()
                null
            }?.forEach { avd ->
                AvdCard(adbDevicePoller, avd)
            }
        }
    }
}
