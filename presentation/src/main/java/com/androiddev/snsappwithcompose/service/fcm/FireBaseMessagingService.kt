package com.androiddev.snsappwithcompose.service.fcm

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.androiddev.data.notification.NotificationHelper
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.model.ChatRequestExtra
import com.androiddev.domain.model.Notification
import com.androiddev.domain.model.NotificationExtra
import com.androiddev.domain.use_case.fcm.FcmTokenUseCase
import com.androiddev.snsappwithcompose.MainActivity
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_CHAT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_COMMENT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_FOLLOW
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_LIKE
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_CHAT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_COMMENT
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_FOLLOW
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_NAME_LIKE
import com.androiddev.snsappwithcompose.common.util.isAppInForeground
import com.androiddev.snsappwithcompose.feature.chat.ChatRequestEventBus
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
    @Inject
    lateinit var notificationHelper: NotificationHelper
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendFcmTokenUseCase.invoke(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        // Notification 메시지 제목/본문

        remoteMessage.data.isNotEmpty().let {
            val data = remoteMessage.data

            val content = data[FcmKeys.CONTENT] ?: ""
            val extraJson = data[FcmKeys.EXTRA_JSON] ?: "{}"
            val date = data[FcmKeys.DATE] ?: ""
            val title = data[FcmKeys.TITLE] ?: ""
            val body = data[FcmKeys.BODY] ?: ""
            val type = data[FcmKeys.TYPE] ?: NotificationType.CHAT
            val chatRequestIdStr = data[FcmKeys.CHAT_REQUEST_ID]

            // 이미 채팅요청목록 화면에 있을때 bus로 전달해서 표시하기 위한 데이터
            val requesterNickname = data[FcmKeys.REQUESTER_NICKNAME] ?: ""
            val requesterGender = data[FcmKeys.REQUESTER_GENDER] ?: ""
            val requesterProfileImage = data[FcmKeys.REQUESTER_PROFILEIMAGE] ?: ""

            val intent: Intent
            val id:Long

            if (chatRequestIdStr != null) {
                //채팅요청 FCM
                id = chatRequestIdStr.toLongOrNull() ?: return
                intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(FcmKeys.CHAT_REQUEST_ID, id)
                    putExtra(FcmKeys.EXTRA_JSON, extraJson)
                }
                val extra = Gson().fromJson(extraJson, ChatRequestExtra::class.java)
                val chatRequestItem = ChatRequest(
                    id = id,
                    requesterId = extra.requesterId,
                    postId = extra.postId,
                    commentId = extra.commentId,
                    createdAt = date,
                    requesterNickname = requesterNickname,
                    requesterGender = requesterGender,
                    requesterProfileimage = requesterProfileImage,
                    content = content
                )
                ChatRequestEventBus.emit(chatRequestItem)
            } else {
                //채팅외 Notification
                id = data[FcmKeys.NOTIFICATION_ID]?.toLong() ?: return

                intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(FcmKeys.NOTIFICATION_ID,id)
                    putExtra(FcmKeys.TYPE, type)
                    putExtra(FcmKeys.EXTRA_JSON, extraJson)
                }
                val extra = Gson().fromJson(extraJson, NotificationExtra::class.java)
                val notificationItem = Notification(
                    id = id,
                    type = type,
                    content = content,
                    extrajson = extra,
                    isRead = false,
                    date = date
                )
                // EventBus로 전달
                NotificationEventBus.emit(notificationItem)
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            if(!isAppInForeground())
                showNotification(id.toInt(),title, body, type, pendingIntent)
        }
    }
    private fun showNotification(
        id:Int,title: String,
        messageBody: String,
        type:String,
        pendingIntent:PendingIntent
    ) {

        Log.d("MyFirebaseMessagingService", "Notification received: $title,")
        // Android 13+ 알림 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return


        val channelId = when(type) {
            NotificationType.CHAT -> CHANNEL_ID_CHAT
            NotificationType.LIKEPOST,NotificationType.LIKECOMMENT -> CHANNEL_ID_LIKE
            NotificationType.COMMENT,NotificationType.REPLY -> CHANNEL_ID_COMMENT
            NotificationType.FOLLOW -> CHANNEL_ID_FOLLOW
            else -> ""
        }
        val channelName = when(type) {
            NotificationType.CHAT -> CHANNEL_NAME_CHAT
            NotificationType.LIKEPOST, NotificationType.LIKECOMMENT -> CHANNEL_NAME_LIKE
            NotificationType.COMMENT, NotificationType.REPLY -> CHANNEL_NAME_COMMENT
            NotificationType.FOLLOW -> CHANNEL_NAME_FOLLOW
            else -> ""
        }
        notificationHelper.createChannel(
            channelId = channelId,
            channelName = channelName,
            importance = NotificationManager.IMPORTANCE_LOW

        )
        val notification = notificationHelper.createNotification(
            channelId = channelId,
            contentTitle = title,
            contentText = messageBody,
            contentIntent = pendingIntent,
            smallIcon = R.drawable.dog
        )
        NotificationManagerCompat.from(this).notify(id,notification)
    }
}