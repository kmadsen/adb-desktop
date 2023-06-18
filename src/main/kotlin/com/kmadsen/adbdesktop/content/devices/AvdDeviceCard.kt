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
import com.kmadsen.adbdesktop.adb.AdbDevicePoller
import com.kmadsen.adbdesktop.adb.AndroidVirtualDevice

@Composable
fun AvdDeviceCard(adbDevicePoller: AdbDevicePoller, avd: AndroidVirtualDevice) {
    Text(text = avd.name)

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = { adbDevicePoller.start(avd) }
        ) {
            Text("start")
        }
    }
}
