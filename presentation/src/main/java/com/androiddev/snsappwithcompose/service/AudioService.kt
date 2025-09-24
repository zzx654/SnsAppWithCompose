package com.androiddev.snsappwithcompose.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.androiddev.snsappwithcompose.R
import okhttp3.OkHttpClient

class AudioService : Service() {

    companion object {
        const val CHANNEL_ID = "audio_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_PLAY = "com.androiddev.snsappwithcompose.ACTION_PLAY"
        const val ACTION_PAUSE = "com.androiddev.snsappwithcompose.ACTION_PAUSE"
        const val ACTION_PLAYBACK_STATUS = "com.androiddev.snsappwithcompose.PLAYBACK_STATUS"

        private var player: ExoPlayer? = null
        private var currentUrl: String? = null
        private var wasPaused = false

        fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return manager.getRunningServices(Int.MAX_VALUE)
                .any { it.service.className == serviceClass.name }
        }

        @JvmStatic
        fun start(context: Context, url: String) {
            val intent = Intent(context, AudioService::class.java).apply {
                action = ACTION_PLAY
                putExtra("url", url)
            }
            if (isServiceRunning(context, AudioService::class.java)) {
                context.startService(intent)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        @JvmStatic
        fun pause(context: Context) {
            val intent = Intent(context, AudioService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }
    }

    private var isForeground = false
    private val handler = Handler(Looper.getMainLooper())

    private val progressUpdater = object : Runnable {
        override fun run() {
            val position = player?.currentPosition ?: 0
            val duration = player?.duration ?: 0
            val isPlaying = player?.isPlaying ?: false

            if (duration > 0 && isPlaying) {
                val progress = ((position * 100) / duration).toInt()
                sendPlaybackStatusBroadcast(true, progress)
                updateNotification(true)
                handler.postDelayed(this, 90)
            } else {
                handler.removeCallbacks(this)
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val isPlaying = player?.isPlaying ?: false

            when (playbackState) {
                Player.STATE_READY -> {
                    if (isPlaying) {
                        handler.post(progressUpdater)
                        updateNotification(true)
                        sendPlaybackStatusBroadcast(true)
                    } else {
                        handler.removeCallbacks(progressUpdater)
                        updateNotification(false)
                        sendPlaybackStatusBroadcast(false)
                    }
                }
                Player.STATE_ENDED -> {
                    player?.pause()
                    player?.seekTo(0)
                    currentUrl = null
                    wasPaused = false
                    handler.removeCallbacks(progressUpdater)
                    updateNotification(false)
                    sendPlaybackStatusBroadcast(false, 100)
                    handler.postDelayed({
                        sendPlaybackStatusBroadcast(false, 0)
                    }, 50)
                }
                Player.STATE_IDLE, Player.STATE_BUFFERING -> {
                    updateNotification(false)
                    sendPlaybackStatusBroadcast(false)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val url = intent.getStringExtra("url") ?: currentUrl

                if (url != null && (player == null || url != currentUrl)) {
                    initializePlayer(url)
                    currentUrl = url
                } else {
                    // ✅ STATE_ENDED 이후 재생을 위한 처리
                    if (player?.playbackState == Player.STATE_ENDED) {
                        player?.seekTo(0)
                        player?.prepare()
                    }
                }

                player?.play()

                // ✅ progressUpdater는 항상 실행
                handler.post(progressUpdater)

                wasPaused = false

                if (!isForeground) {
                    startForeground(NOTIFICATION_ID, createNotification(true))
                    isForeground = true
                } else {
                    updateNotification(true)
                }

                sendPlaybackStatusBroadcast(true)
            }

            ACTION_PAUSE -> {
                player?.pause()
                wasPaused = true
                handler.removeCallbacks(progressUpdater)
                updateNotification(false)
                sendPlaybackStatusBroadcast(false)
            }

            else -> {
                if (player?.isPlaying == true && !isForeground) {
                    startForeground(NOTIFICATION_ID, createNotification(true))
                    isForeground = true
                }
            }
        }

        return START_STICKY
    }

    @UnstableApi
    private fun initializePlayer(url: String) {
        val okHttpClient = OkHttpClient.Builder().build()
        val okHttpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        val dataSourceFactory = DefaultDataSource.Factory(this, okHttpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        player?.release()

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        player?.addListener(playerListener)
        player?.setMediaItem(MediaItem.fromUri(url))
        player?.prepare()
    }

    private fun createNotification(isPlaying: Boolean): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.notification_audio)
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY

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

        val duration = player?.duration ?: 0
        val position = player?.currentPosition ?: 0
        val progress = if (duration > 0) ((position * 100) / duration).toInt() else 0
        remoteViews.setProgressBar(R.id.notification_progress, 100, progress, false)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mic)
            .setCustomContentView(remoteViews)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .build()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(isPlaying)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendPlaybackStatusBroadcast(isPlaying: Boolean, progress: Int = -1) {
        val intent = Intent(ACTION_PLAYBACK_STATUS).apply {
            putExtra("isPlaying", isPlaying)
            if (progress >= 0) {
                putExtra("progress", progress)
            } else {
                val duration = player?.duration ?: 0
                val position = player?.currentPosition ?: 0
                val progressPercent = if (duration > 0) ((position * 100) / duration).toInt() else 0
                putExtra("progress", progressPercent)
            }
        }
        sendBroadcast(intent)
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

    override fun onDestroy() {
        handler.removeCallbacks(progressUpdater)
        player?.release()
        player = null
        isForeground = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}