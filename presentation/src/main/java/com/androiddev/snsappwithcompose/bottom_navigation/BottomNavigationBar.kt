package com.androiddev.snsappwithcompose.bottom_navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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

@SuppressLint("RestrictedApi")
@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = BottomBar,
        elevation = 10.dp
    ) {
        items.forEach { item ->

            val selected = backStackEntry.value?.destination?.hasRoute(item.route::class) == true
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