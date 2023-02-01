package com.kmadsen.adbdesktop.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
        buildTopBar()
        buildDrawerContent()
    }
}

@Composable
fun buildTopBar() {
    TopAppBar(
        title = { Text("adb desktop") }
    )
}

@Composable
fun buildDrawerContent() {
    Row {
        buildDrawer()
        buildSurface()
    }
}

@Composable
fun buildDrawer() {
    val adbDevicePoller = rememberAdbDeviceManager()

    var connectedDevices by remember { mutableStateOf(emptyList<AdbDevice>()) }
    adbDevicePoller.poll { freshDevices -> connectedDevices = freshDevices }
    var androidVirtualDevices by remember { mutableStateOf(emptyList<AndroidVirtualDevice>())}
    adbDevicePoller.request { freshAvds -> androidVirtualDevices = freshAvds }

    Column(
        modifier = Modifier
            .width(300.dp)

    ) {
        Text(
            text = "Connected devices",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        connectedDevices.ifEmpty {
            buildEmptyDeviceCard()
            null
        }?.forEach { deviceId ->
            buildAdbDeviceCard(adbDevicePoller, deviceId)
        }
        Text(
            text = "Emulator list",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
        androidVirtualDevices.ifEmpty {
            buildEmptyDeviceCard()
            null
        }?.forEach { avd ->
            buildAvdCard(adbDevicePoller, avd)
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
    )
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
                .heightIn(50.dp),
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
                .heightIn(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            buildDeviceRow(adbDevicePoller, adbDevice)
        }
    }
}

@Composable
fun buildAvdCard(adbDevicePoller: AdbDevicePoller, avd: AndroidVirtualDevice) {
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
            buildAdbDeviceCard(adbDevicePoller, avd)
        }
    }
}

@Composable
fun buildDeviceRow(adbDevicePoller: AdbDevicePoller, adbDevice: AdbDevice) {
    println("buildDeviceRow $adbDevice")
    Column(
        modifier = Modifier.widthIn(max = 120.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = adbDevice.deviceId)
        adbDevice.adbWifiState.ipAddress?.let { Text(text = it) }
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

@Composable
fun buildAdbDeviceCard(adbDevicePoller: AdbDevicePoller, avd: AndroidVirtualDevice) {
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
