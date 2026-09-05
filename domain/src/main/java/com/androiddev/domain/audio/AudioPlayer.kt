package com.androiddev.domain.audio

import java.io.File

interface AudioPlayer {
    fun start(file: File, onCompletion: () -> Unit): Long // 재생 총 길이(ms) 반환
    fun stop()
}