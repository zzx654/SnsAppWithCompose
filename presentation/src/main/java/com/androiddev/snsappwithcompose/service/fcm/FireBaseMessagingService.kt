package com.androiddev.snsappwithcompose.service.fcm

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.androiddev.domain.model.NotificationExtra
import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.use_case.fcm.FcmTokenUseCase
import com.androiddev.domain.util.elapsedTime
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_COMMENT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_FOLLOW
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_LIKE
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_COMMENT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_FOLLOW
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_LIKE
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_COMMENT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_FOLLOW
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_LIKE
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import com.androiddev.snsappwithcompose.feature.notification.NotificationEventBus
import com.androiddev.snsappwithcompose.feature.notification.NotificationType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var sendFcmTokenUseCase: FcmTokenUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendFcmTokenUseCase.invoke(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Notification 메시지 제목/본문
        val notifTitle = remoteMessage.notification?.title
        val notifBody = remoteMessage.notification?.body
        remoteMessage.data.isNotEmpty().let {
            val data = remoteMessage.data
            val id = data[FcmKeys.NOTIFICATION_ID]?.toLong() ?: return
            val type = data[FcmKeys.TYPE] ?: return
            val content = data[FcmKeys.CONTENT] ?: ""
            val extraJson = data[FcmKeys.EXTRA_JSON] ?: "{}"
            val date = data[FcmKeys.DATE] ?: ""

            val extra = Gson().fromJson(extraJson, NotificationExtra::class.java)
            // data 메시지
            val dataTitle = remoteMessage.notification?.title
            val dataBody = remoteMessage.notification?.body

            val title = notifTitle ?: dataTitle ?: ""
            val body = notifBody ?: dataBody ?: ""


            sendNotification(title, body, type)
            val notificationItem = NotificationItem(
                id = id,
                type = type,
                content = content,
                extrajson = extra,
                isRead = false,
                date = date,
                elapsedTime = elapsedTime(date)
            )

            // EventBus로 전달
            NotificationEventBus.emit(notificationItem)
        }
    }





    private fun sendNotification(title: String, messageBody: String, type:String) {

        Log.d("MyFirebaseMessagingService", "Notification received: $title,")
        // Android 13+ 알림 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val notificationHelper = NotificationHelper(this)

        val channelId = when(type) {
            NotificationType.LIKEPOST,NotificationType.LIKECOMMENT -> CHANNEL_ID_LIKE
            NotificationType.COMMENT,NotificationType.REPLY -> CHANNEL_ID_COMMENT
            NotificationType.FOLLOW -> CHANNEL_ID_FOLLOW
            else -> ""
        }
        val notificationChannelId = when(type) {
            NotificationType.LIKEPOST,NotificationType.LIKECOMMENT -> NOTIFICATION_ID_LIKE
            NotificationType.COMMENT,NotificationType.REPLY -> NOTIFICATION_ID_COMMENT
            NotificationType.FOLLOW -> NOTIFICATION_ID_FOLLOW
            else -> 0
        }
        val channelName = when(type) {
            NotificationType.LIKEPOST, NotificationType.LIKECOMMENT -> CHANNEL_NAME_LIKE
            NotificationType.COMMENT, NotificationType.REPLY -> CHANNEL_NAME_COMMENT
            NotificationType.FOLLOW -> CHANNEL_NAME_FOLLOW
            else -> ""
        }
        notificationHelper.createChannel(
            channelId = channelId,
            channelName = channelName,
            importance =
                if(type == NotificationType.LIKEPOST||type == NotificationType.LIKECOMMENT) NotificationManager.IMPORTANCE_LOW
                else NotificationManager.IMPORTANCE_HIGH
        )
        val notification = notificationHelper.createNotification(
            channelId = channelId,
            contentTitle = title,
            contentText = messageBody,
        )
        NotificationManagerCompat.from(this).notify(notificationChannelId,notification)

    }
}