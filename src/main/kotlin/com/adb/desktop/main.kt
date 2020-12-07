package com.adb.desktop

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme

fun main() = Window(
    title = "ADB Desktop",
    icon = icAppRounded()
) {
    MaterialTheme {
        DesktopTheme {
            BuildAppUI()
        }
    }
}
