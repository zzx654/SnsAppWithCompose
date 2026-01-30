package com.androiddev.snsappwithcompose.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.androiddev.snsappwithcompose.feature.notification.NotificationViewModel

@Composable
fun PendingNotificationHandler(
    notificationViewModel: NotificationViewModel
) {
    val pending by notificationViewModel.pending.collectAsState()

    LaunchedEffect(pending) {
        if(pending != null)
            notificationViewModel.consumePending()

    }
}