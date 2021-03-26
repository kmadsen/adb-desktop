package com.adb.desktop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
                modifier = Modifier.width(400.dp)
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

            buildSurface()
        }
    }
}

@Composable
fun buildSurface() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = RoundedCornerShape(10f),
        color = Color.Yellow,
        contentColor = Color.Green,
        border = BorderStroke(8.dp, Color.Black),
        elevation = 4.dp,
        content = {
            buildCanvas()
        }
    )
}

@Composable
fun buildCanvas() {
    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
        drawLine(
            color = Color.Black,
            start = Offset(0.0f, 0.0f),
            end = Offset(600f, 600f),
            strokeWidth = 50.0f,
            cap = StrokeCap.Butt,
            pathEffect = null,
            alpha = 1.0f,
            colorFilter = null,
            blendMode = BlendMode.Color
        )
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
