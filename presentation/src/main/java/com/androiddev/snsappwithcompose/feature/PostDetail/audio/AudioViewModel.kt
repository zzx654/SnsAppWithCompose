package com.androiddev.snsappwithcompose.feature.PostDetail.audio

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.audio.AudioPlayer
import com.androiddev.domain.audio.AudioServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
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
    private val audioPlayer: AudioPlayer,
    private val audioServiceController: AudioServiceController,
    private val context: Context
) : ViewModel() {

    //ViewModel → Service: RemoteViews 갱신, Toggle 요청

    //Service → ViewModel: Playback 상태 / Progress 전달


    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress



    fun prepareAudio(url: String, nickname: String) {
        audioServiceController.registerToggleReceiver {
            toggle()
        }
        setupPlayer(url)
        audioServiceController.startServicePrepare(url, nickname)
    }
    fun toggle() {
        if (_isPlaying.value) {
            pauseAudio()
        } else {
            startAudio()
        }
        audioServiceController.sendStatusToService(_isPlaying.value, _progress.value)
    }
    private fun setupPlayer(url: String) {
        audioPlayer.prepare(url) {
            onAudioCompleted()
        }
        _progress.value = 0f
        _isPlaying.value = false
        startProgressLoop()
    }

    private fun startAudio() {
        audioPlayer.start()
        _isPlaying.value = true
    }

    private fun pauseAudio() {
        audioPlayer.pause()
        _isPlaying.value = false
    }

    private fun onAudioCompleted() {
        _isPlaying.value = false
        _progress.value = 0f
        audioServiceController.sendStatusToService(false, 0f)
    }

    private fun startProgressLoop() {
        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            while (isActive) {
                if (_isPlaying.value) {
                    _progress.value = audioPlayer.currentProgress
                    audioServiceController.sendStatusToService(_isPlaying.value, _progress.value)
                }
                delay(200L)
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        audioServiceController.unregisterToggleReceiver()
        audioServiceController.stopAudioService()

        progressJob?.cancel()
        audioPlayer.release()
    }
}
