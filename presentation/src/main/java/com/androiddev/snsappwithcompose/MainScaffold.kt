package com.androiddev.snsappwithcompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androiddev.snsappwithcompose.bottom_navigation.BottomNavItem
import com.androiddev.snsappwithcompose.bottom_navigation.BottomNavigationBar
import com.androiddev.snsappwithcompose.home.HomeScreen
import com.androiddev.snsappwithcompose.navigation.components.Screen
import com.androiddev.snsappwithcompose.navigation.components.Screen.HomeScreen
import androidx.activity.compose.BackHandler
import com.androiddev.snsappwithcompose.home.tags.TagViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScaffold(
    rootNavController: NavHostController,
    startTab: Screen = Screen.HomeScreen,
    tagViewModel: TagViewModel
) {
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(color = Color.Gray, thickness = 0.5.dp)
                BottomNavigationBar(
                    items = listOf(
                        BottomNavItem("홈", HomeScreen, Icons.Default.Home),
                        BottomNavItem("글쓰기", Screen.UploadPostScreen(), Icons.Default.Create),
                    ),
                    initialScreen = Screen.InitScreen::class,
                    navController = tabNavController,
                    onItemClick = {
                        if(it.route == Screen.UploadPostScreen()) {
                            rootNavController.navigate(it.route)
                        } else {
                            tabNavController.navigate(it.route) {
                                //if (it.route != Screen.UploadPostScreen) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                                // }
                            }
                        }
                    }
                )
            }
        }

    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = startTab,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Screen.HomeScreen> {
                BackHandler(true) {
                }
                HomeScreen(navController = rootNavController, tagViewModel = tagViewModel)
            }
        }

    }
}