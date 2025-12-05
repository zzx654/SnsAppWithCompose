package com.androiddev.snsappwithcompose.common.navigation.bottom_navigation.component

import androidx.compose.ui.graphics.vector.ImageVector
import com.androiddev.snsappwithcompose.common.navigation.component.Screen

data class BottomNavItem(
    val name: String,
    val route: Screen,
    val icon: ImageVector,
    val badge: Boolean = false
)