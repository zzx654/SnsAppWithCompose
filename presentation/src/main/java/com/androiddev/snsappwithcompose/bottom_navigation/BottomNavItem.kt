package com.androiddev.snsappwithcompose.bottom_navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.androiddev.snsappwithcompose.navigation.components.Screen

data class BottomNavItem(
    val name: String,
    val route: Screen,
    val icon: ImageVector
)