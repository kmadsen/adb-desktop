package com.kmadsen.adbdesktop.content

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kmadsen.adbdesktop.AppContext
import com.kmadsen.adbdesktop.content.devices.DevicesContent
import com.kmadsen.adbdesktop.content.logcat.AdbLogcatContent
import com.kmadsen.adbdesktop.sidebar.NavItem

@Composable
fun MainContentArea(appContext: AppContext) {
    val navItem by appContext.navItemsManager.current.collectAsState()

    println("NavItem changed $navItem")
    when (navItem) {
        NavItem.Devices -> DevicesContent(appContext)
        NavItem.Files -> Text("Files screen")
        NavItem.Logcat -> AdbLogcatContent(appContext)
        NavItem.Shell -> Text("Shell screen")
        NavItem.Settings -> Text("Settings screen")
    }
}
