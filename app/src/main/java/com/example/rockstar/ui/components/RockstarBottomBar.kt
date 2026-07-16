package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.navigation.bottomNavItems
import com.example.rockstar.ui.theme.RockstarAccent
import com.example.rockstar.ui.theme.RockstarBackground
import com.example.rockstar.ui.theme.RockstarTextSecondary

@Composable
fun RockstarBottomBar(
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit
) {
    NavigationBar(
        containerColor = RockstarBackground,
        tonalElevation = 8.dp,
        modifier = Modifier.height(80.dp)
    ) {
        bottomNavItems.forEach { item ->
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                selected = currentRoute == item.destination.route,
                onClick = { onTabSelected(item.destination) },
                icon = { Icon(item.icon, contentDescription = label) },
                label = { Text(label, style = androidx.compose.material3.MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RockstarAccent,
                    selectedTextColor = RockstarAccent,
                    unselectedIconColor = RockstarTextSecondary,
                    unselectedTextColor = RockstarTextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
