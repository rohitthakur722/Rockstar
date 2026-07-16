package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rockstar.navigation.RockstarDestination
import com.example.rockstar.ui.theme.RockstarBackground

/**
 * Shared app shell for the five bottom-navigation tab destinations.
 */
@Composable
fun RockstarAppScaffold(
    title: String,
    currentRoute: String,
    onTabSelected: (RockstarDestination) -> Unit,
    modifier: Modifier = Modifier,
    showBrandMark: Boolean = false,
    trailingIcon: ImageVector? = null,
    trailingContentDescription: String? = null,
    onTrailingClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = RockstarBackground,
        topBar = {
            RockstarTopBar(
                title = title,
                showBrandMark = showBrandMark,
                trailingIcon = trailingIcon,
                trailingContentDescription = trailingContentDescription,
                onTrailingClick = onTrailingClick
            )
        },
        bottomBar = { RockstarBottomBar(currentRoute = currentRoute, onTabSelected = onTabSelected) },
        content = content
    )
}
