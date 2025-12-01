package com.androiddev.snsappwithcompose.service.audio

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.NotificationConstants
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_AUDIO
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_AUDIO
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import com.androiddev.snsappwithcompose.common.util.NotificationPermissionUtils
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys
//import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys.ISPLAYING
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys.NICKNAME
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys.PROGRESS
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys.URL
import kotlinx.coroutines.*


class AudioService : Service() {

    private lateinit var notificationHelper: NotificationHelper
    private var rviews: RemoteViews? = null
    private var nicknameText: String = ""


    private val playbackStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action != ACTION_PLAYBACK_STATUS) return

            val isPlaying = intent.getBooleanExtra(ISPLAYING, false)
            val progress = intent.getIntExtra(PROGRESS, 0)
            println("$isPlaying $progress")
            // 권한 체크 후 진행률 UI 업데이트
            NotificationPermissionUtils.checkNotificationPermission(
                context = this@AudioService,
                onGranted = {
                    updateRemoteView(isPlaying, progress)
                }
            )
        }
    }

    companion object {
        const val ACTION_PREPARE = "com.androiddev.snsappwithcompose.ACTION_PREPARE"
        const val ACTION_TOGGLEPLAYBACK = "com.androiddev.snsappwithcompose.ACTION_TOGGLEPLAYBACK"
        const val ACTION_PLAYBACK_STATUS = "com.androiddev.snsappwithcompose.ACTION_PLAYBACK_STATUS"
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        notificationHelper = NotificationHelper(this)

        // ViewModel의 진행률/재생상태 수신
        registerReceiver(playbackStatusReceiver, IntentFilter(ACTION_PLAYBACK_STATUS))
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action) {
                ACTION_PREPARE -> {

                    val nickname = intent.getStringExtra(NICKNAME) ?: ""
                    nicknameText = "$nickname 님의 음성"

                    // 권한 있을 때만 Notification 표시 (Compose UI는 별개로 계속 작동)
                    NotificationPermissionUtils.checkNotificationPermission(
                        context = this@AudioService,
                        onGranted = {
                            rviews = createRemoteView()
                            startForegroundSafe()
                        }
                    )
                }
                ACTION_TOGGLEPLAYBACK -> {

                    val toggleIntent = Intent(ACTION_TOGGLEPLAYBACK)
                    sendBroadcast(toggleIntent)
                }
            }
        }

        return START_STICKY
    }

    private fun startForegroundSafe() {
        notificationHelper.createChannel(
            CHANNEL_ID_AUDIO,
            getString(R.string.audio_service_channel_name)
        )

        rviews?.let {
            val notification = notificationHelper.createNotification(
                channelId = CHANNEL_ID_AUDIO,
                smallIcon = android.R.drawable.ic_btn_speak_now,
                contentView = it,
                contentTitle = getString(R.string.app_name),
                contentText = nicknameText
            )

            startForeground(NOTIFICATION_ID_AUDIO, notification)
        }
    }

    private fun createRemoteView(): RemoteViews {
        val rv = RemoteViews(packageName, R.layout.notification_audio)

        rv.setTextViewText(R.id.txt_title, nicknameText)
        rv.setImageViewResource(R.id.btn_play_pause, R.drawable.ic_play)
        rv.setProgressBar(R.id.mediaProgress, 100, 0, false)

        val toggleIntent = Intent(this, AudioService::class.java).apply {
            action = ACTION_TOGGLEPLAYBACK
        }
        val togglePendingIntent = PendingIntent.getService(
            this, 0, toggleIntent, PendingIntent.FLAG_IMMUTABLE
        )
        rv.setOnClickPendingIntent(R.id.btn_play_pause, togglePendingIntent)

        return rv
    }

    fun updateRemoteView(isPlaying: Boolean, progress: Int) {
        rviews?.apply {
            setProgressBar(R.id.mediaProgress, 100, progress, false)
            setImageViewResource(
                R.id.btn_play_pause,
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        rviews?.let {
            notificationHelper.updateNotification(
                NOTIFICATION_ID_AUDIO,
                contentView = it
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(playbackStatusReceiver)

        notificationHelper.cancelNotification(NOTIFICATION_ID_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}