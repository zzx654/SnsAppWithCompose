package com.androiddev.snsappwithcompose.upload_post

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.data.service.RecordService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val context: Context
): ViewModel() {
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    val progress: StateFlow<Float> = elapsedTime.map { it / 60f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)



    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val elapsed = intent?.getIntExtra("elapsed", 0) ?: return
            _elapsedTime.value = elapsed
            if (elapsed >= 60) stopRecording(context!!)
        }
    }

    init {
        context.registerReceiver(receiver, IntentFilter("RECORDING_ELAPSED"),
            Context.RECEIVER_NOT_EXPORTED)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording(context: Context) {
        if (_isRecording.value) return



        println("startㄹㅋㄷ")
        _isRecording.value = true
        _elapsedTime.value = 0

        context.startForegroundService(Intent(context, RecordService::class.java).apply {
            action = "START"
        })
    }

    fun stopRecording(context: Context) {
        println("stopㄹㅋㄷ")
        _isRecording.value = false
        context.startService(Intent(context, RecordService::class.java).apply {
            action = "STOP"
        })
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(receiver)
    }
}