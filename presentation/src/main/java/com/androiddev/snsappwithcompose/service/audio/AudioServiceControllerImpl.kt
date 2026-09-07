package com.androiddev.snsappwithcompose.service.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.androiddev.domain.audio.AudioServiceController
import com.androiddev.snsappwithcompose.common.util.NotificationPermissionUtils
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AudioServiceControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioServiceController {

    private var toggleReceiver: BroadcastReceiver? = null

    override fun registerToggleReceiver(onToggle: () -> Unit) {
        unregisterToggleReceiver() // 중복 등록 방지

        toggleReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == AudioService.ACTION_TOGGLEPLAYBACK) {
                    onToggle()
                }
            }
        }

        val filter = IntentFilter(AudioService.ACTION_TOGGLEPLAYBACK)
        ContextCompat.registerReceiver(
            context,
            toggleReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun unregisterToggleReceiver() {
        toggleReceiver?.let {
            runCatching { context.unregisterReceiver(it) }
            toggleReceiver = null
        }
    }

    override fun startServicePrepare(url: String, nickname: String) {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onGranted = {
                val intent = Intent(context, AudioService::class.java).apply {
                    action = AudioService.ACTION_PREPARE
                    putExtra(AudioIntentKeys.URL, url)
                    putExtra(AudioIntentKeys.NICKNAME, nickname)
                }
                ContextCompat.startForegroundService(context, intent)
            }
        )
    }

    override fun stopAudioService() {
        val intent = Intent(context, AudioService::class.java).apply {
            action = AudioService.ACTION_FINISH
        }
        context.startService(intent)
    }

    override fun sendStatusToService(isPlaying: Boolean, progress: Float) {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onGranted = {
                val intent = Intent(AudioService.ACTION_PLAYBACK_STATUS).apply {
                    putExtra(AudioIntentKeys.ISPLAYING, isPlaying)
                    putExtra(AudioIntentKeys.PROGRESS, (progress * 100).toInt())
                }
                context.sendBroadcast(intent)
            }
        )
    }
}