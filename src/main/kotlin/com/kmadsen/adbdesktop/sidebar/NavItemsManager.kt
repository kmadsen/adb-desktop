package com.kmadsen.adbdesktop.sidebar

import kotlinx.coroutines.flow.MutableStateFlow

class NavItemsManager {
    val navItems = listOf(
        NavItem.Devices,
        NavItem.Files,
        NavItem.Logcat,
        NavItem.Shell,
        NavItem.Settings
    )
    val current = MutableStateFlow(navItems[0])
}
