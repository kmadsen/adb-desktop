package com.adb.desktop

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val adb = Adb(Terminal())

@Composable
fun rememberAdbDeviceManager(): AdbDevicePoller {

    val coroutineScope = rememberCoroutineScope()
    return AdbDevicePoller(adb, coroutineScope)
}

@Composable
fun BuildAppUI() {
    Column {
        val adbDevicePoller = rememberAdbDeviceManager()

        TopAppBar(
            title = { Text("adb desktop") },
        )
        var refresh by remember { mutableStateOf(emptyList<AdbDevice>()) }
        adbDevicePoller.poll {
            refresh = it
        }

        ScrollableColumn {
            for (deviceId in refresh) {
                buildAdbDeviceCard(adbDevicePoller, deviceId)
            }
        }
    }

}

@Composable
fun buildAdbDeviceCard(adbDevicePoller: AdbDevicePoller, adbDevice: AdbDevice) {
    println("buildAdbDeviceCard $adbDevice")

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(4.dp)
            .width(450.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            adbDeviceRow(adbDevicePoller, adbDevice)
        }
    }

}

@Composable
fun adbDeviceRow(adbDevicePoller: AdbDevicePoller, adbDevice: AdbDevice) {
    Text(
        text = adbDevice.deviceId,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .width(200.dp)
    )
    val wifiState = adbDevice.adbWifiState
    if (wifiState.connected) {
        Button(
            modifier = Modifier.padding(8.dp)
                .width(180.dp),
            onClick = { adbDevicePoller.disconnect(adbDevice) }
        ) {
            Text("disconnect wifi")
        }
    } else if (wifiState.ipAddress != null) {
        Button(
            modifier = Modifier.padding(8.dp)
                .width(180.dp),
            onClick = { adbDevicePoller.connect(adbDevice) }
        ) {
            Text("connect wifi")
        }
    }
}
