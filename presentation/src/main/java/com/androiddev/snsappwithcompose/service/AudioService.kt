package com.androiddev.snsappwithcompose.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import com.androiddev.snsappwithcompose.R

class AudioService : Service() {

    companion object {
        const val CHANNEL_ID = "audio_channel"
        const val NOTIFICATION_ID = 1

        private var player: ExoPlayer? = null
        private var currentUrl: String? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun start(context: Context, url: String) {
            val intent = Intent(context, AudioService::class.java).apply {
                action = "ACTION_PLAY"
                putExtra("url", url)
            }
            context.startForegroundService(intent)
        }

        fun pause(context: Context) {
            val intent = Intent(context, AudioService::class.java).apply {
                action = "ACTION_PAUSE"
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            "ACTION_PLAY" -> {
                val url = intent.getStringExtra("url") ?: currentUrl
                if (url != null && url != currentUrl) {
                    initializePlayer(url)
                    currentUrl = url
                }
                player?.play()
                startForeground(NOTIFICATION_ID, createNotification(true))
                sendPlaybackStatusBroadcast(true)
            }
            "ACTION_PAUSE" -> {
                player?.pause()
                updateNotification(false)
                sendPlaybackStatusBroadcast(false)
            }
            else -> {
                if (player != null && player!!.isPlaying) {
                    startForeground(NOTIFICATION_ID, createNotification(true))
                }
            }
        }
        return START_STICKY
    }

    private fun initializePlayer(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
        }
        player?.setMediaItem(MediaItem.fromUri(url))
        player?.prepare()
    }

    private fun createNotification(isPlaying: Boolean): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.notification_audio)

        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val action = if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY"

        val pendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, AudioService::class.java).apply { this.action = action },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        remoteViews.setImageViewResource(R.id.notification_icon, iconRes)
        remoteViews.setOnClickPendingIntent(R.id.notification_icon, pendingIntent)
        remoteViews.setTextViewText(
            R.id.notification_text,
            if (isPlaying) "재생 중입니다" else "일시정지됨"
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mic)
            .setCustomContentView(remoteViews)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val notification = createNotification(isPlaying)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendPlaybackStatusBroadcast(isPlaying: Boolean) {
        val intent = Intent("com.example.audioservice.PLAYBACK_STATUS")
        intent.putExtra("isPlaying", isPlaying)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}