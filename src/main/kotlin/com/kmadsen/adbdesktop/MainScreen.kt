package com.kmadsen.adbdesktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kmadsen.adbdesktop.content.MainContentArea
import com.kmadsen.adbdesktop.sidebar.SideBarNavigation

@Composable
fun MainScreen() {
    val appContext = AppContext()
    val useDarkTheme: Boolean = isSystemInDarkTheme()
    appContext.themeManager.setTheme(useDarkTheme)
    val colorScheme by appContext.themeManager.theme.collectAsState()

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header()
            Row(
                modifier = Modifier.weight(1f)
            ) {
                SideBarNavigation(appContext)
                MainContentArea(appContext)
            }
            Footer()
        }
    }
}

@Composable
fun Header() {
    Text("I'm a header")
}

@Composable
fun Footer() {
    Text("I'm a footer")
}
