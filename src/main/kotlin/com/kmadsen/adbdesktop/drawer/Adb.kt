package com.kmadsen.adbdesktop.drawer

class Adb(
    private val terminal: Terminal
) {
    suspend fun version(): String {
        val cmd = "adb --version"
        val result = terminal.run(cmd)

        val regex = """Android Debug Bridge version (\d+).(\d+).(\d+)""".toRegex()
        val matchResult = result
            .map { line -> regex.find(line) }
            .firstOrNull()
            ?: return "Unknown"
        val (major, minor, release) = matchResult.destructured

        return "$major.$minor.$release"
    }

    suspend fun devices(): List<String> {
        val cmd = "adb devices"
        val result = terminal.run(cmd)

        val regex = """(.+)(device)""".toRegex()
        if (result.firstOrNull() != "List of devices attached") {
            return emptyList()
        }
        return result
            .filter(String::isNotEmpty)
            .drop(1)
            .mapNotNull { line -> regex.find(line) }
            .map { matchResult -> matchResult.groupValues[1].trim() }
    }

    suspend fun wifiState(deviceId: String): AdbWifiState {
        deviceIdToAdbWifiState(deviceId)?.let { return it }

        val cmd = "adb -s $deviceId shell ip route"
        val result = terminal.run(cmd)

        val regex = """(\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b)$""".toRegex()
        val ipAddress = result
            .filter { line -> line.contains("dev wlan0") }
            .map { line -> regex.find(line)?.value }
            .firstOrNull()
            ?: return WIFI_UNAVAILABLE
        val isConnected = deviceId.contains("adb-tls-connect")
        return AdbWifiState(isConnected, ipAddress, null)
    }

    private fun deviceIdToAdbWifiState(deviceId: String): AdbWifiState? {
        val regex = """(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)""".toRegex()
        val matchResult = regex.find(deviceId)
            ?: return null
        val (ipAddress, port) = matchResult.destructured
        return AdbWifiState(true, ipAddress, port)
    }

    suspend fun connect(deviceId: String, ipAddress: String): AdbWifiState {
        val port = "5555"
        val commandSetPort = "adb -s $deviceId tcpip $port"
        terminal.run(commandSetPort)
        val commandConnect = "adb -s $deviceId connect $ipAddress:$port"
        terminal.run(commandConnect)

        return AdbWifiState(
            false,
            ipAddress,
            port
        )
    }

    suspend fun disconnect(deviceId: String) {
        val cmd = "adb disconnect $deviceId"
        terminal.run(cmd)
    }

    suspend fun listAvds(): List<AndroidVirtualDevice> {
        val androidHome = System.getenv()["ANDROID_HOME"]
        val cmd = "$androidHome/emulator/emulator -list-avds"
        val result = terminal.run(cmd)
        return result.map { name -> AndroidVirtualDevice(name) }
    }

    suspend fun start(avd: AndroidVirtualDevice) {
        val androidHome = System.getenv()["ANDROID_HOME"]
        val cmd = "$androidHome/emulator/emulator -avd ${avd.name}"
        terminal.run(cmd)
    }

    suspend fun killEmulator(adbDevice: AdbDevice) {
        val cmd = "adb -s ${adbDevice.deviceId} emu kill"
        terminal.run(cmd)
    }
}

data class AdbWifiState(
    val connected: Boolean,
    val ipAddress: String?,
    val port: String?,
)

val WIFI_UNAVAILABLE = AdbWifiState(false, null, null)
