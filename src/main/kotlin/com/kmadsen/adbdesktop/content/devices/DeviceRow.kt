package com.kmadsen.adbdesktop.content.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmadsen.adbdesktop.adb.AdbDevice
import com.kmadsen.adbdesktop.adb.AdbDevicePoller

@Composable
fun DeviceRow(adbDevicePoller: AdbDevicePoller, adbDevice: AdbDevice) {
    println("buildDeviceRow $adbDevice")
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "DeviceId: ${adbDevice.deviceId}")
        adbDevice.adbWifiState.ipAddress?.let { ipAddress ->
            Text(text = "Ip Address: $ipAddress")
        }
    }
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        if (adbDevice.isEmulator()) {
            Button(
                onClick = { adbDevicePoller.killEmulator(adbDevice) }
            ) {
                Text("kill")
            }
        }
        if (adbDevice.adbWifiState.connected) {
            Button(
                onClick = { adbDevicePoller.disconnect(adbDevice) }
            ) {
                Text("disconnect")
            }
        } else if (adbDevice.adbWifiState.ipAddress != null) {
            Button(
                onClick = { adbDevicePoller.connect(adbDevice) }
            ) {
                Text("connect wifi")
            }
        }
    }
}
