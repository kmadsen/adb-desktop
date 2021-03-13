package com.adb.desktop

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
        check(result[0] == "List of devices attached")
        return result
            .filter(String::isNotEmpty)
            .drop(1)
            .mapNotNull { line -> regex.find(line) }
            .map { matchResult -> matchResult.groupValues[1].trim() }
    }

    fun isConnected(deviceId: String): AdbWifiState? {
        val regex = """(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)""".toRegex()
        val matchResult = regex.find(deviceId)
            ?: return null
        val (ipAddress, port) = matchResult.destructured
        return AdbWifiState(true, ipAddress, port)
    }

    suspend fun wifiState(deviceId: String): AdbWifiState {
        val checkConnected = isConnected(deviceId)
        if (checkConnected != null) return checkConnected

        val cmd = "adb -s $deviceId shell ip route"
        val result = terminal.run(cmd)

        val regex = """(\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b)$""".toRegex()
        val ipAddress = result
            .filter { line -> line.contains("dev wlan0") }
            .map { line -> regex.find(line)?.value }
            .firstOrNull()
            ?: return WIFI_UNAVAILABLE

        return AdbWifiState(false, ipAddress, null)
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

    suspend fun disconnect(adbDevice: AdbDevice) {
        val address = "${adbDevice.adbWifiState.ipAddress}:${adbDevice.adbWifiState.port}"
        val cmd = "adb disconnect $address"
        val result = terminal.run(cmd)
        println(result)
    }
}

data class AdbWifiState(
    val connected: Boolean,
    val ipAddress: String?,
    val port: String?,
)

val WIFI_UNAVAILABLE = AdbWifiState(false, null, null)
