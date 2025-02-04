package com.androiddev.snsappwithcompose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.androiddev.snsappwithcompose.navigation.components.Navigation
import com.androiddev.snsappwithcompose.ui.theme.Background
import com.androiddev.snsappwithcompose.ui.theme.SnsAppWithComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SnsAppWithComposeTheme {

                val yearListState = rememberLazyListState(2005 - 1955)
                val year by remember { derivedStateOf { yearListState.firstVisibleItemIndex + 1955 } }
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusable(true)
                        .navigationBarsPadding(),
                    color = Background
                ) {
                    val navController = rememberNavController()
                    Box(modifier = Modifier.fillMaxSize()) {
                        Navigation(navController = navController)
                    }
                }
            }
        }
    }
}


