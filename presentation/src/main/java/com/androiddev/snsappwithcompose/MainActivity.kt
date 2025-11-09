package com.androiddev.snsappwithcompose

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.androiddev.snsappwithcompose.common.navigation.component.Navigation
import com.androiddev.snsappwithcompose.ui.theme.SnsAppWithComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.androiddev.snsappwithcompose.common.viewmodel.UserViewModel
import com.androiddev.snsappwithcompose.common.util.createNotificationChannel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi", "UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val navController = rememberNavController()
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            SnsAppWithComposeTheme {
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color(0xFFF8F5F5),
                        darkIcons = true
                    )
                }
                Surface(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable(true)
                        .navigationBarsPadding()
                        .systemBarsPadding()
                ) {
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null
                    ) {
                        val userViewModel: UserViewModel = hiltViewModel()

                        Navigation(
                            navController = navController,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                }
            }
        }
    }
}
