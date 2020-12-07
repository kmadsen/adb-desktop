package com.adb.desktop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

data class AdbDevice(
    val deviceId: String,
    val adbWifiState: AdbWifiState
)

class AdbDevicePoller(
    private val adb: Adb,
    private val coroutineScope: CoroutineScope
) {
    private val id = UUID.randomUUID()

    private val maxDelay = 5000L
    private var currentDelay = maxDelay

    private var callback: (List<AdbDevice>) -> Unit = { }

    fun poll(callback: (List<AdbDevice>) -> Unit) {
        this@AdbDevicePoller.callback = callback
        coroutineScope.launch {
            while (isActive) {
                devices()
                println("$id polling devices $currentDelay")
                delay(currentDelay)
                currentDelay = min((currentDelay * 1.50).toLong(), maxDelay)
            }
            this@AdbDevicePoller.callback = { }
        }
    }

    fun connect(adbDevice: AdbDevice) = coroutineScope.launch {
        val ipAddress = adbDevice.adbWifiState.ipAddress ?: return@launch
        adb.connect(adbDevice.deviceId, ipAddress)
        invalidate()
    }

    fun disconnect(adbDevice: AdbDevice) = coroutineScope.launch {
        adb.disconnect(adbDevice)
        invalidate()
    }

    private fun devices() = coroutineScope.launch {
        val devices = adb.devices()
            .map { deviceId ->
                val wifiState = adb.wifiState(deviceId)
                AdbDevice(deviceId, wifiState)
            }
        if (isActive) {
            callback(devices)
        }
    }

    fun invalidate() {
        devices()
        currentDelay = 500L
    }
}