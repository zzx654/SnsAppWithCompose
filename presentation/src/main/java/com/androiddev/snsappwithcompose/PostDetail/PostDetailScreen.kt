package com.androiddev.snsappwithcompose.PostDetail

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.ScreenWithTopBar
import com.androiddev.snsappwithcompose.upload_post.UploadPostEvent
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.fetchLocation

@Composable
fun PostDetailScreen(
    post: PostPreview?,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    ScreenWithTopBar(
        topBar = {
            CenterAlignedTopBar(
                title = post!!.nickname,
                onBackClick = { navController.popBackStack() },
            )
        },
        focusManager = focusManager,
        content = {},
        bottomBar = {}
    )
    /**Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = post!!.text,
            modifier = Modifier.clickable{ navController.popBackStack()}
        )
    }**/
}