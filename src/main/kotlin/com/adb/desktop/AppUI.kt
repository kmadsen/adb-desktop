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
fun buildAppUI() {
    Column {
        val adbDevicePoller = rememberAdbDeviceManager()

        TopAppBar(
            title = { Text("adb desktop") }
        )
        var refresh by remember { mutableStateOf(emptyList<AdbDevice>()) }
        adbDevicePoller.poll {
            refresh = it
        }

        Row {
            ScrollableColumn(
                modifier = Modifier.width(300.dp)
            ) {
                Text(
                    text = "Devices",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                refresh.ifEmpty {
                    buildEmptyDeviceCard()
                    null
                }?.forEach { deviceId ->
                    buildAdbDeviceCard(adbDevicePoller, deviceId)
                }
            }

            // TODO add the main page here
        }
    }
}

@Composable
fun buildEmptyDeviceCard() {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("No devices detected")
        }
    }
}

@Composable
fun buildAdbDeviceCard(adbDevicePoller: AdbDevicePoller, adbDevice: AdbDevice) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
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
    Text(text = adbDevice.deviceId)
    val wifiState = adbDevice.adbWifiState
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        if (wifiState.connected) {
            Button(
                onClick = { adbDevicePoller.disconnect(adbDevice) }
            ) {
                Text("disconnect wifi")
            }
        } else if (wifiState.ipAddress != null) {
            Button(
                onClick = { adbDevicePoller.connect(adbDevice) }
            ) {
                Text("connect wifi")
            }
        }
    }
}
