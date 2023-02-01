package com.kmadsen.adbdesktop.drawer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

class AdbDevicePoller(
    private val adb: Adb,
    private val coroutineScope: CoroutineScope
) {
    private val id = UUID.randomUUID()
    private var currentDelay = MAX_DELAY_MS

    private var callback: (List<AdbDevice>) -> Unit = { }

    fun poll(callback: (List<AdbDevice>) -> Unit) {
        this@AdbDevicePoller.callback = callback
        coroutineScope.launch {
            while (isActive) {
                devices()
                println("$id polling devices $currentDelay")
                delay(currentDelay)
                currentDelay = min((currentDelay * 1.50).toLong(), MAX_DELAY_MS)
            }
            this@AdbDevicePoller.callback = { }
        }
    }

    fun request(callback: (List<AndroidVirtualDevice>) -> Unit) = coroutineScope.launch {
        callback(adb.listAvds())
    }

    fun start(avd: AndroidVirtualDevice) = coroutineScope.launch {
        adb.start(avd)
        invalidate()
    }

    fun connect(adbDevice: AdbDevice) = coroutineScope.launch {
        val ipAddress = adbDevice.adbWifiState.ipAddress ?: return@launch
        adb.connect(adbDevice.deviceId, ipAddress)
        invalidate()
    }

    fun disconnect(adbDevice: AdbDevice) = coroutineScope.launch {
        adb.disconnect(adbDevice.deviceId)
        invalidate()
    }

    fun killEmulator(adbDevice: AdbDevice) = coroutineScope.launch {
        adb.killEmulator(adbDevice)
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

    private fun invalidate() {
        devices()
        currentDelay = 500L
    }

    companion object {
        private const val MAX_DELAY_MS = 5000L
    }
}
