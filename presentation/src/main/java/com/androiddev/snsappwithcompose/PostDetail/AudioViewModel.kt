package com.androiddev.snsappwithcompose.PostDetail

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.service.AudioService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AudioViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var receiverRegistered = false

    private val playbackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isPlaying = intent?.getBooleanExtra("isPlaying", false) ?: false
            val progress = intent?.getIntExtra("progress", 0) ?: 0

            _isPlaying.value = isPlaying
            _progress.value = progress / 100f
        }
    }

   // init {
   //     registerReceiver()
   // }
    fun registerReceiver() {
        if (receiverRegistered) return

        val filter = IntentFilter(AudioService.ACTION_PLAYBACK_STATUS)
        context.registerReceiver(playbackReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        receiverRegistered = true
    }
    fun setAudioAvailable(isAvailable: Boolean,url:String?,nickname:String?) {
        if (isAvailable) {
            if (url != null && nickname != null) {
                registerReceiver()
                prepare(url, nickname)
            }
        } else {
            if (receiverRegistered) {
                context.unregisterReceiver(playbackReceiver)
                receiverRegistered = false
            }
        }
    }
    fun toggle() {
        sendCommand(AudioService.ACTION_TOGGLEPLAYBACK)
    }
    fun prepare(url:String,nickname:String) {
        sendCommand(AudioService.ACTION_PREPARE,url,nickname)
    }
    private fun sendCommand(action: String, url: String? = null, nickname: String? = null) {
        val intent = Intent(context, AudioService::class.java).apply {
            url?.let{ putExtra("url",it)}
            nickname?.let{ putExtra("nickname",it)}
            this.action = action
        }
        if(action == AudioService.ACTION_PREPARE)
            ContextCompat.startForegroundService(context, intent)
        else
            context.startService(intent)
    }

    override fun onCleared() {
        super.onCleared()
        if (receiverRegistered) {
            context.unregisterReceiver(playbackReceiver)
            receiverRegistered = false
            context.stopService(Intent(context, AudioService::class.java))
        }
    }
}