package com.androiddev.snsappwithcompose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.androiddev.snsappwithcompose.navigation.components.Navigation
import com.androiddev.snsappwithcompose.ui.theme.Background
import com.androiddev.snsappwithcompose.ui.theme.SnsAppWithComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.Screen.HomeScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.androiddev.snsappwithcompose.bottom_navigation.BottomNavItem
import com.androiddev.snsappwithcompose.bottom_navigation.BottomNavigationBar
import com.androiddev.snsappwithcompose.util.Screen.UploadPostScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("RestrictedApi", "UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SnsAppWithComposeTheme {
                var isBottomBarVisible = true
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
                        .navigationBarsPadding(),
                    color = Background
                ) {
                    Scaffold(
                        bottomBar = {
                            if(isBottomBarVisible) {
                                BottomNavigationBar(
                                    items = listOf(
                                        BottomNavItem(
                                            name = "홈",
                                            route = Screen.HomeScreen,
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
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Navigation(navController = navController)

                        }
                    }


                }
            }
        }
    }

}


