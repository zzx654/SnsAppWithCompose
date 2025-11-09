package com.androiddev.snsappwithcompose.common.navigation.bottom_navigation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.androiddev.snsappwithcompose.ui.theme.BottomBar
import com.androiddev.snsappwithcompose.ui.theme.BottomSelected
import com.androiddev.snsappwithcompose.ui.theme.BottomUnSelected
import androidx.navigation.NavDestination.Companion.hasRoute
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import kotlin.reflect.KClass

@SuppressLint("RestrictedApi")
@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    initialScreen: KClass<out Screen>,   // 초기 탭을 Screen 클래스 타입으로 받음
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    BottomNavigation(
        modifier = modifier,
        backgroundColor = BottomBar,
        elevation = 0.dp
    ) {
        items.forEach { item ->
            // 초기 상태이거나, 현재 destination과 일치하면 selected = true
            val selected = currentDestination?.hasRoute(item.route::class) ?: (item.route::class == initialScreen)
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = BottomSelected,
                unselectedContentColor = BottomUnSelected,
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name
                        )
                        if(selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }
}