package com.androiddev.snsappwithcompose.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.androiddev.snsappwithcompose.feature.notification.NotificationViewModel

@Composable
fun MainScreenPendingHandler(
    notificationViewModel: NotificationViewModel,
    isPostsLoaded: Boolean
) {
    val pending by notificationViewModel.pending.collectAsState()

    LaunchedEffect(pending, isPostsLoaded) {
        if (pending != null && isPostsLoaded) {
            notificationViewModel.consumePending()
        }
    }
}