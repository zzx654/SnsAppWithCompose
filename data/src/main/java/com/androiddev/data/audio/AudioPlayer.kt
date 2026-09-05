package com.androiddev.data.audio

import android.content.Context
import android.media.MediaPlayer
import com.androiddev.domain.audio.AudioPlayer
import java.io.File
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(
    private val context: Context
) : AudioPlayer {

    private var player: MediaPlayer? = null

    override fun start(file: File, onCompletion: () -> Unit): Long {
        var durationMs = 0L
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            durationMs = duration.toLong()
            setOnCompletionListener {
                stop()
                onCompletion()
            }
            start()
        }
        return durationMs
    }

    override fun stop() {
        player?.runCatching {
            stop()
            release()
        }
        player = null
    }
}