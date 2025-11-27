package com.androiddev.snsappwithcompose.common.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.service.audio.AudioService.Companion.NOTIFICATION_ID

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

        // 설정 파라미터들
       // priority: Int = NotificationCompat.PRIORITY_DEFAULT,
       // visibility: Int = NotificationCompat.VISIBILITY_PRIVATE,
        isForegroundNotification: Boolean = false,

       // category: String? = null,
       // defaults: Int = 0,
        //autoCancel: Boolean = false,
       // ongoing: Boolean = false
    ): Notification {

        notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)  // 벡터 아이콘
            //.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_play_white)) // 큰 아이콘
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setAutoCancel(!isForegroundNotification) // 간단한 설정만
            .setOnlyAlertOnce(isForegroundNotification)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            //.setCustomContentView(contentView)
            .setContentIntent(contentIntent)
            //.setAutoCancel(autoCancel)
            .setOngoing(isForegroundNotification)
            .setContent(contentView)
            //.setPriority(priority)
            //.setVisibility(visibility)
            //.setColor(0xff000000.toInt())
            //.setShowWhen(true)

        /**if (defaults != 0) {
            builder.setDefaults(defaults)
        }

        if (category != null) {
            builder.setCategory(category)
        }**/

        return notificationBuilder.build()
    }
}