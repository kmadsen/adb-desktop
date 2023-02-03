package com.kmadsen.adbdesktop

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.kmadsen.adbdesktop.apk.ApkUi
import com.kmadsen.adbdesktop.drawer.buildDrawer
import com.kmadsen.adbdesktop.drawer.buildSurface

object AppUi {
    @Composable
    fun buildApp() {
        Row {
            buildDrawer()
            ApkUi.buildDirectorySelect()
            buildSurface()
        }
    }
}
