package com.androiddev.snsappwithcompose.service.fcm

import android.Manifest
import com.androiddev.snsappwithcompose.R
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import com.androiddev.snsappwithcompose.feature.notification.NotificationEventBus
import com.androiddev.snsappwithcompose.service.audio.AudioService.Companion.ACTION_TOGGLEPLAYBACK
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Notification 메시지 제목/본문
        val notifTitle = remoteMessage.notification?.title
        val notifBody = remoteMessage.notification?.body

        // data 메시지
        val dataTitle = remoteMessage.data["title"]
        val dataBody = remoteMessage.data["body"]
        //val dataType = remoteMessage.data["type"]

        // 우선순위: Notification 메시지 > data 메시지
        val title = notifTitle ?: dataTitle ?: "새 메시지"
        val body = notifBody ?: dataBody ?: "메시지가 도착했습니다."

        NotificationEventBus.emit(true)
        sendNotification(title, body)


    }

    private fun sendNotification(title: String, messageBody: String) {

        Log.d("MyFirebaseMessagingService", "Notification received: $title,")
        // Android 13+ 알림 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createChannel(
            channelId = "default_channel_id",
            channelName = "메시지 알림",
            importance = NotificationManager.IMPORTANCE_HIGH
        )
        val notification = notificationHelper.createNotification(
            channelId = "default_channel_id",
            //smallIcon = android.R.drawable.sym_def_app_icon,
            contentTitle = title,
            contentText = messageBody,
            //smallIcon = TODO(),
           // contentView = TODO(),
        )
        NotificationManagerCompat.from(this).notify(1,notification)

    }
}