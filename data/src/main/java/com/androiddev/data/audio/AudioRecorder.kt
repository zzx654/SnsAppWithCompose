package com.androiddev.data.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.androiddev.domain.audio.AudioRecorder
import java.io.File
import javax.inject.Inject

class AndroidAudioRecorder @Inject constructor(
    private val context: Context
) : AudioRecorder {

    private var recorder: MediaRecorder? = null

    override fun start(outputFile: File) {
        recorder = createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    override fun stop() {
        recorder?.runCatching {
            stop()
            release()
        }
        recorder = null
    }

    @Suppress("DEPRECATION")
    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
    }
}