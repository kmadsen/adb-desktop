package com.kmadsen.adbdesktop

import com.kmadsen.adbdesktop.adb.Adb
import com.kmadsen.adbdesktop.adb.Terminal
import com.kmadsen.adbdesktop.sidebar.NavItemsManager
import com.kmadsen.adbdesktop.theme.ThemeManager

class AppContext {
    val themeManager = ThemeManager()
    val navItemsManager = NavItemsManager()

    val adb = Adb(Terminal())
}
