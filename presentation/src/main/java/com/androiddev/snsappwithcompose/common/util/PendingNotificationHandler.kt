package com.androiddev.snsappwithcompose.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.COMMENT
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.LIKECOMMENT
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.LIKEPOST
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.REPLY
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

@Composable
fun PendingNotificationHandler(
    notificationViewModel: NotificationViewModel,
    isSignedIn: Boolean,
    isHomeReady: Boolean
) {
    val pending by notificationViewModel.pending.collectAsStateWithLifecycle()

    LaunchedEffect(pending, isSignedIn, isHomeReady) {
        val currentPending = pending ?: return@LaunchedEffect

        if (!isSignedIn) return@LaunchedEffect

        // 게시글 연관 알림 타입 판별
        val isPostRelated = currentPending.type in listOf(LIKEPOST, COMMENT, REPLY,LIKECOMMENT)

        if (isPostRelated) {
            // 게시글 관련 알림은 Home 피드가 완전 로드되었을 때 소비
            if (isHomeReady) {
                notificationViewModel.consumePending()
            }
        } else {
            // 팔로우 등 피드 로딩과 무관한 알림은 즉시 소비
            notificationViewModel.consumePending()
        }
    }
}