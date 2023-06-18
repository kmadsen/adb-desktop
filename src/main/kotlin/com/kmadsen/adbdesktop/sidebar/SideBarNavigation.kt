package com.kmadsen.adbdesktop.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmadsen.adbdesktop.AppContext

@Composable
fun SideBarNavigation(appContext: AppContext) {
    val navItemsRepository = appContext.navItemsManager
    val navItems = navItemsRepository.navItems
    val selectedItem by navItemsRepository.current.collectAsState()

    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        navItems.forEach { navItem ->
            SideBarItem(
                icon = navItem.icon,
                text = navItem.text,
                isSelected = navItem == selectedItem,
                onClick = { navItemsRepository.current.value = navItem }
            )
        }
    }
}
