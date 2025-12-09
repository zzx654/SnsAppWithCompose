package com.androiddev.snsappwithcompose.common.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.androiddev.snsappwithcompose.R

class NotificationHelper(private val context: Context) {

    private val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var notificationBuilder:NotificationCompat.Builder

    fun createChannel(
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_LOW
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            manager.createNotificationChannel(channel)
        }
    }
    fun cancelNotification(
        notiID:Int
    ) {
        manager.cancel(notiID)
    }
    fun updateNotification(
        notiId:Int = 1,
        contentText:String?= null,
        contentView: RemoteViews? = null,
    ) {
        notificationBuilder.apply {
            contentText?.let { setContentText(contentText) }
            setContent(contentView)
        }
        notificationBuilder.setContentText(contentText)
        manager.notify(notiId, notificationBuilder.build())
    }

    fun createNotification(
        channelId: String,
        smallIcon: Int = R.drawable.dog,
        contentView: RemoteViews? = null,
        contentTitle: String? = null,
        contentText: String? = null,
        contentIntent: PendingIntent? = null,
        isForegroundNotification: Boolean = false,
    ): Notification {

        notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)  // 벡터 아이콘
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setAutoCancel(!isForegroundNotification)
            .setOnlyAlertOnce(isForegroundNotification)
            .setContentTitle(contentTitle)
            .setContentIntent(contentIntent)
            .setOngoing(isForegroundNotification)
            .setContent(contentView)
        if (contentText.isNullOrBlank()) {
            notificationBuilder.setContentText(null)
        } else {
            notificationBuilder.setContentText(contentText)
        }

        return notificationBuilder.build()
    }
}