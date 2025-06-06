package com.androiddev.snsappwithcompose

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.androiddev.snsappwithcompose.navigation.components.Navigation
import com.androiddev.snsappwithcompose.ui.theme.SnsAppWithComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.androiddev.snsappwithcompose.navigation.components.Screen
import com.androiddev.snsappwithcompose.navigation.components.Screen.HomeScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.androiddev.snsappwithcompose.bottom_navigation.BottomNavItem
import com.androiddev.snsappwithcompose.bottom_navigation.BottomNavigationBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @SuppressLint("RestrictedApi", "UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SnsAppWithComposeTheme {
                var isBottomBarVisible = false
                val navController = rememberNavController()
                val navBackStackEntryState = navController.currentBackStackEntryAsState()
                navBackStackEntryState.value?.destination?.let {
                    isBottomBarVisible = it.hasRoute(HomeScreen::class)
                    //||it.hasRoute(UploadPostScreen::class)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable(true)
                        .navigationBarsPadding().systemBarsPadding()
                ) {
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = !isSystemInDarkTheme()

                    SideEffect {
                        systemUiController.setStatusBarColor(
                            color = Color(0xFFF8F5F5),
                            darkIcons = !useDarkIcons
                        )
                    }
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if(isBottomBarVisible) {
                                Column {
                                    HorizontalDivider(color = Color.Gray,thickness = 0.5.dp)
                                    BottomNavigationBar(
                                        items = listOf(
                                            BottomNavItem(
                                                name = "홈",
                                                route = HomeScreen,
                                                icon = Icons.Default.Home
                                            ),
                                            BottomNavItem(
                                                name = "글쓰기",
                                                route = Screen.UploadPostScreen,
                                                icon = Icons.Default.Create
                                            ),
                                        ),
                                        navController = navController,
                                        onItemClick = {

                                            navController.navigate(it.route) {
                                                if(it.route != Screen.UploadPostScreen) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    // 같은아이템 재선택시 같은 desination 생성 방지
                                                    launchSingleTop = true
                                                    //아이템 선택시 state 복구
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }

                            }
                        }
                    ) { contentPadding->
                        CompositionLocalProvider(
                            LocalOverscrollConfiguration provides null
                        ) {
                            Navigation(navController = navController,modifier = Modifier.padding(contentPadding))

                        }
                    }
                }
            }
        }
    }

}