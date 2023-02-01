package com.kmadsen.adbdesktop.drawer

data class AdbDevice(
    val deviceId: String,
    val adbWifiState: AdbWifiState
) {
    fun isEmulator(): Boolean = deviceId.startsWith("emulator-")
}
