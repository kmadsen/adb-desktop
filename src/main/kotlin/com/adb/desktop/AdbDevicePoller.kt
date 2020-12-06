package com.adb.desktop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min

data class AdbDevice(
    val deviceId: String,
    val adbWifiState: AdbWifiState
)

class AdbDevicePoller(
    private val adb: Adb
) {
    private val maxDelay = 5000L
    private var currentDelay = maxDelay

    private var callback: (List<AdbDevice>) -> Unit = { }

    fun poll(coroutineScope: CoroutineScope, callback: (List<AdbDevice>) -> Unit) {
        coroutineScope.launch {
            while (isActive) {
                callback(devices())
                println("polling devices $currentDelay")
                delay(currentDelay)
                currentDelay = min((currentDelay*1.50).toLong(), maxDelay)
            }
        }
    }

    fun connect(adbDevice: AdbDevice) {
        val ipAddress = adbDevice.adbWifiState.ipAddress ?: return
        adb.connect(adbDevice.deviceId, ipAddress)
        invalidate()
    }

    fun disconnect(adbDevice: AdbDevice) {
        adb.disconnect(adbDevice)
        invalidate()
    }

    private fun devices(): List<AdbDevice> {
        return adb.devices()
            .map { deviceId ->
                val wifiState = adb.wifiState(deviceId)
                AdbDevice(deviceId, wifiState)
            }
    }

    fun invalidate() {
        callback(devices())
        currentDelay = 500L
    }
}