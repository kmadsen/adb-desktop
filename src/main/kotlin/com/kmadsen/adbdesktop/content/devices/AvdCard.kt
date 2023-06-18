package com.kmadsen.adbdesktop.content.devices

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmadsen.adbdesktop.adb.AdbDevicePoller
import com.kmadsen.adbdesktop.adb.AndroidVirtualDevice

@Composable
fun AvdCard(adbDevicePoller: AdbDevicePoller, avd: AndroidVirtualDevice) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .heightIn(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvdDeviceCard(adbDevicePoller, avd)
        }
    }
}
