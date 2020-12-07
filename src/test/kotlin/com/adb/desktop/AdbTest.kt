package com.adb.desktop

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdbTest {

    private val terminal = mockk<Terminal>()
    private val adb = Adb(terminal)

    @Test
    fun `version should get the adb version`() = runBlocking {
        val testOutput = listOf(
            "Android Debug Bridge version 1.0.41",
            "Version 30.0.3-6597393",
            """Installed as C:\Users\Travis\AppData\Local\Android\Sdk\platform-tools\adb.exe"""
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.version()

        assertEquals("1.0.41", result)
    }

    @Test
    fun `devices should be empty if no devices are connected`() = runBlocking {
        val testOutput = listOf(
            "List of devices attached",
            ""
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.devices()

        assertTrue(result.isEmpty(), result.joinToString())
    }

    @Test
    fun `devices should be have all devices`() = runBlocking {
        val testOutput = listOf(
            "List of devices attached",
            "98281FFBA0088H    device",
            "192.168.188.150:5555    device",
            "2E6408CC437E0002  device",
            "emulator-5554     device",
            ""
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.devices()

        assertEquals(4, result.size)
        assertEquals("98281FFBA0088H", result[0])
        assertEquals("192.168.188.150:5555", result[1])
        assertEquals("2E6408CC437E0002", result[2])
        assertEquals("emulator-5554", result[3])
    }

    @Test
    fun `wifiState detects when device id is not found`() = runBlocking {
        val testOutput = listOf(
            "device '98281FFBA88H' not found"
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.wifiState("98281FFBA88H")

        assertFalse(result.connected)
    }

    @Test
    fun `wifiState returns false if there is no dev wlan0`() = runBlocking {
        val testOutput = listOf(
            "192.168.200.0/24 dev if11 proto kernel scope link src 192.168.200.2",
            "192.168.232.0/21 dev if3 proto kernel scope link src 192.168.232.2"
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.wifiState("98281FFBA88H")

        assertFalse(result.connected)
    }

    @Test
    fun `wifiState detects when wifi is available`() = runBlocking {
        val testOutput = listOf(
            "192.168.188.0/24 dev wlan0 proto kernel scope link src 192.168.188.150"
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.wifiState("98281FFBA0088H")

        assertFalse(result.connected)
        assertEquals("192.168.188.150", result.ipAddress)
    }

    @Test
    fun `wifiState detects when wifi is connected`() = runBlocking {
        val testOutput = listOf(
            "192.168.188.0/24 dev wlan0 proto kernel scope link src 192.168.188.150"
        )
        coEvery { terminal.run(any()) } returns testOutput

        val result = adb.wifiState("192.168.188.150:5555")

        assertTrue(result.connected)
        assertEquals("192.168.188.150", result.ipAddress)
        assertEquals("5555", result.port)
    }
}
