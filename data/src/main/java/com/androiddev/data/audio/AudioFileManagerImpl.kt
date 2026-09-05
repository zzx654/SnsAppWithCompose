package com.androiddev.data.audio

import android.content.Context
import com.androiddev.data.util.FileUtil
import com.androiddev.domain.audio.AudioFileManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AudioFileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioFileManager {
    override fun generateRecordFile(): File {
        return FileUtil.generateFile(context)
    }
}