package com.androiddev.snsappwithcompose.service.audio

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_AUDIO
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_AUDIO
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import kotlinx.coroutines.*

class AudioService : Service() {

    private lateinit var notificationHelper: NotificationHelper
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var rviews: RemoteViews

    private var currentUrl: String? = null
    private var nicknameText: String = ""

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null
    private var lastNotificationUpdateTime = 0L

    companion object {
        const val ACTION_PREPARE = "com.androiddev.snsappwithcompose.ACTION_PREPARE"
        const val ACTION_TOGGLEPLAYBACK = "com.androiddev.snsappwithcompose.ACTION_TOGGLEPLAYBACK"
        const val ACTION_PLAYBACK_STATUS = "com.androiddev.snsappwithcompose.PLAYBACK_STATUS"
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PREPARE -> {
                    val url = it.getStringExtra("url") ?: currentUrl
                    val nickname = it.getStringExtra("nickname") ?: ""
                    if (url != null) setMedia(path = url, nickname = nickname)
                }
                ACTION_TOGGLEPLAYBACK -> {
                    togglePlayPause()
                }
            }
        }
        return START_STICKY
    }

    private fun setMedia(path: String, nickname: String) {
        currentUrl = path
        nicknameText = "$nickname 님의 음성"
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            setOnCompletionListener {
                onPlaybackComplete()
            }
        }
        isPlaying = false
        rviews = createRemoteView()
        startForegroundService()
    }

    private fun startForegroundService() {
        notificationHelper.createChannel(
            channelId = CHANNEL_ID_AUDIO,
            channelName = getString(R.string.audio_service_channel_name)
        )

        val notification = notificationHelper.createNotification(
            channelId = CHANNEL_ID_AUDIO,
            smallIcon = android.R.drawable.ic_btn_speak_now,
            contentView = rviews,
            contentTitle = getString(R.string.app_name),
            contentText = nicknameText

        )
        startForeground(NOTIFICATION_ID_AUDIO, notification)
    }

    private fun createRemoteView(): RemoteViews {
        val remoteView = RemoteViews(packageName, R.layout.notification_audio)

        remoteView.setTextViewText(R.id.txt_title, nicknameText)
        remoteView.setImageViewResource(
            R.id.btn_play_pause,
            getPlayPauseIconRes()
        )
        remoteView.setProgressBar(
            R.id.mediaProgress,
            mediaPlayer?.duration ?: 0,
            mediaPlayer?.currentPosition ?: 0,
            false
        )

        val toggleIntent = Intent(this, AudioService::class.java).apply {
            action = ACTION_TOGGLEPLAYBACK
        }
        val togglePendingIntent = PendingIntent.getService(this, 0, toggleIntent, PendingIntent.FLAG_IMMUTABLE)
        remoteView.setOnClickPendingIntent(R.id.btn_play_pause, togglePendingIntent)

        return remoteView
    }

    private fun togglePlayPause() {
        if (mediaPlayer == null) return
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
        isPlaying = true
        startProgressLoop()
        updateNotificationUI()
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        isPlaying = false
        stopProgressLoop()
        updateNotificationUI()
        sendPlaybackStatusBroadcast(isPlaying = false, progress = getProgressPercent())
    }
    private fun getPlayPauseIconRes(): Int {
        val isDarkMode = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val playIcon = if (isDarkMode) R.drawable.ic_play_white else R.drawable.ic_play
        val pauseIcon = if (isDarkMode) R.drawable.ic_pause_white else R.drawable.ic_pause
        return if (isPlaying) pauseIcon else playIcon
    }
    private fun onPlaybackComplete() {
        isPlaying = false
        stopProgressLoop()

        // 직접 위치 0으로 설정
        rviews.setProgressBar(R.id.mediaProgress, 100, 0, false)
        rviews.setImageViewResource(R.id.btn_play_pause, getPlayPauseIconRes())
        rviews.setTextViewText(R.id.txt_title, nicknameText)
        notificationHelper.updateNotification(
            notiId = NOTIFICATION_ID_AUDIO,
            contentView = rviews

        )
        sendPlaybackStatusBroadcast(isPlaying = false, progress = 0)
    }

    private fun startProgressLoop() {
        progressJob?.cancel()
        lastNotificationUpdateTime = 0L
        progressJob = scope.launch {
            while (isPlaying && mediaPlayer != null) {
                delay(20L) // UI 빠른 업데이트

                val progress = getProgressPercent()
                sendPlaybackStatusBroadcast(isPlaying = true, progress = progress)

                // Notification UI는 300ms 간격으로 업데이트
                val now = System.currentTimeMillis()
                if (now - lastNotificationUpdateTime > 250) {
                    lastNotificationUpdateTime = now
                    updateNotificationUI()
                }
            }
        }
    }

    private fun stopProgressLoop() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun getProgressPercent(): Int {
        val duration = mediaPlayer?.duration ?: 0
        val position = mediaPlayer?.currentPosition ?: 0
        return if (duration > 0) (position * 100 / duration) else 0
    }

    private fun updateNotificationUI() {
        rviews = createRemoteView()
        notificationHelper.updateNotification(
            contentView = rviews
        )
    }

    private fun sendPlaybackStatusBroadcast(isPlaying: Boolean, progress: Int) {
        val intent = Intent(ACTION_PLAYBACK_STATUS).apply {
            putExtra("isPlaying", isPlaying)
            putExtra("progress", progress)
        }
        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        notificationHelper.cancelNotification(NOTIFICATION_ID_AUDIO)
    }
}