package com.androiddev.snsappwithcompose.feature.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.component.TabPager
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.feature.home.nearposts.NearPostsScreen
import com.androiddev.snsappwithcompose.feature.home.newPosts.NewPostsScreen
import com.androiddev.snsappwithcompose.feature.home.tags.TagScreen
import com.androiddev.snsappwithcompose.feature.home.tags.TagViewModel
import com.androiddev.snsappwithcompose.feature.upload_post.record.RecordEvent

@Composable
fun HomeScreen(navController: NavController, tagViewModel: TagViewModel) {
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        checkPermissions(
            context,
            permissions,
            onUnGranted = {
                launcherMultiplePermissions.launch(permissions)
            }
        )
    }
    val tabs = listOf("근처", "인기", "새로운","팔로우","태그","사람")
    val pages = listOf<@Composable () -> Unit>(
        {NearPostsScreen(navController)},
        {PlaceholderScreen("인기")},
        {NewPostsScreen(navController)},
        {PlaceholderScreen("팔로우")},
        {TagScreen(navController, tagViewModel)},
        {PlaceholderScreen("사람")}
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TabPager(
            tabs = tabs,
            pages = pages,
            startIndex = tabs.indexOf("새로운")
        )
    }
}
@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title 화면 준비중...", fontSize = 20.sp)
    }
}