package com.adb.desktop

data class AdbDevice(
    val deviceId: String,
    val adbWifiState: AdbWifiState
) {
    fun isEmulator(): Boolean = deviceId.startsWith("emulator-")
}
