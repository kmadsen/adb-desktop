package com.kmadsen.adbdesktop.sidebar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val text: String,
    val icon: ImageVector,
) {
    object Devices : NavItem("Devices", Icons.Default.PlayArrow)
    object Files : NavItem("Files", Icons.Default.AccountBox)
    object Logcat : NavItem("Logcat", Icons.Default.Build)
    object Shell : NavItem("Shell", Icons.Default.List)
    object Settings : NavItem("Settings", Icons.Default.Settings)
}
