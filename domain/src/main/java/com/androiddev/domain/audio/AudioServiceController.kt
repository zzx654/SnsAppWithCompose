package com.androiddev.domain.audio

interface AudioServiceController {

    fun registerToggleReceiver(onToggle: () -> Unit)

    fun unregisterToggleReceiver()

    fun startServicePrepare(url: String, nickname: String)

    fun stopAudioService()

    fun sendStatusToService(isPlaying: Boolean, progress: Float)
}