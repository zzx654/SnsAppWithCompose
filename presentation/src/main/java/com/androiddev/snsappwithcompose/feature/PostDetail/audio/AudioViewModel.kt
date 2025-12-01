package com.androiddev.snsappwithcompose.feature.PostDetail.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.androiddev.snsappwithcompose.common.util.NotificationPermissionUtils
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys.NICKNAME
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioIntentKeys.URL
import com.androiddev.snsappwithcompose.service.audio.AudioService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
object AudioIntentKeys {
    const val URL = "url"
    const val NICKNAME = "nickname"
    const val ISPLAYING = "isPlaying"
    const val PROGRESS = "progress"
}
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AudioViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    //ViewModel → Service: RemoteViews 갱신, Toggle 요청

    //Service → ViewModel: Playback 상태 / Progress 전달

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress


    private val toggleReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != AudioService.ACTION_TOGGLEPLAYBACK) return
            toggle()
        }
    }

    fun prepareAudio(url: String, nickname: String) {
        registerToggleReceiver()
        setupPlayer(url)
        startServicePrepare(url,nickname)
    }
    fun toggle() {
        mediaPlayer?.let {
            if (_isPlaying.value) pauseAudio()
            else startAudio()
        }
        sendStatusToService()
    }
    private fun setupPlayer(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            setOnCompletionListener { onAudioCompleted() }
        }
        _progress.value = 0f
        _isPlaying.value = false
        startProgressLoop()
    }

    private fun startAudio() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    private fun onAudioCompleted() {
        _isPlaying.value = false
        _progress.value = 0f
        sendStatusToService()
    }

    private fun startProgressLoop() {
        progressJob?.cancel()

        progressJob = scope.launch {
            while (true) {
                val player = mediaPlayer ?: break

                if (player.isPlaying) {
                    _progress.value = player.currentPosition / player.duration.toFloat()
                    sendStatusToService()
                }
                delay(200L)
            }
        }
    }

    private fun sendStatusToService() {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onGranted = {
                val intent = Intent(AudioService.ACTION_PLAYBACK_STATUS).apply {
                    putExtra(AudioIntentKeys.ISPLAYING, _isPlaying.value)
                    putExtra(AudioIntentKeys.PROGRESS, (_progress.value * 100).toInt())
                }
                context.sendBroadcast(intent)
            }
        )
    }

    private fun startServicePrepare(url: String, nickname: String) {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onGranted = {
                val intent = Intent(context, AudioService::class.java).apply {
                    action = AudioService.ACTION_PREPARE
                    putExtra(URL, url)
                    putExtra(NICKNAME, nickname)
                }
                ContextCompat.startForegroundService(context,intent)
            }
        )
    }


    private fun registerToggleReceiver() {
        val filter = IntentFilter(AudioService.ACTION_TOGGLEPLAYBACK)
        ContextCompat.registerReceiver(
            context,
            toggleReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onCleared() {
        super.onCleared()
        runCatching { context.unregisterReceiver(toggleReceiver) }

        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
