package com.androiddev.data.audio

import android.content.Context
import android.media.MediaPlayer
import com.androiddev.domain.audio.AudioPlayer
import java.io.File
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(
) : AudioPlayer {

    private var player: MediaPlayer? = null
    override val currentProgress: Float
        get() {
            val p = player ?: return 0f
            return runCatching {
                if (p.duration > 0) {
                    p.currentPosition.toFloat() / p.duration.toFloat()
                } else {
                    0f
                }
            }.getOrDefault(0f)
        }
    override fun start(file: File, onCompletion: () -> Unit): Long {
        player?.release()
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

    override fun start() {
        player?.start()
    }

    override fun prepare(url: String, onCompletion: () -> Unit) {
        player?.release()
        player = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            setOnCompletionListener { onCompletion() }
        }
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.runCatching {
            stop()
            release()
        }
        player = null
    }

    override fun release() {
        player?.release()
        player = null

    }
}