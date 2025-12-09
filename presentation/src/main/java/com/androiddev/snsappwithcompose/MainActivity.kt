package com.androiddev.snsappwithcompose

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.androiddev.snsappwithcompose.common.navigation.component.Navigation
import com.androiddev.snsappwithcompose.ui.theme.SnsAppWithComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import com.androiddev.snsappwithcompose.common.util.createNotificationChannel
import org.w3c.dom.Text

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi", "UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // createNotificationChannel(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 권한 요청 콜백 등록
        val requestNotificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // 권한 허용됨
                } else {
                    // 권한 거부됨
                }
            }
        setContent {

            val navController = rememberNavController()
            val useDarkIcons = !isSystemInDarkTheme()
            val selectionColors = TextSelectionColors(
                handleColor = Color.Gray,         // 선택 핸들의 색상
                backgroundColor = Color.LightGray // 선택 영역 배경의 색상
            )

            SnsAppWithComposeTheme {
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
                        LocalTextSelectionColors provides selectionColors,
                        LocalOverscrollFactory provides null
                    ) {
                        Navigation(
                            navController = navController,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                }
            }
        }
        // Android 13 이상이면 알림 권한 요청
    }

}
