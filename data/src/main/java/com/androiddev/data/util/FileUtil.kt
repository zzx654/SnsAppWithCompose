package com.androiddev.data.util

import android.content.Context
import java.io.File

object FileUtil {

    fun generateFile(context: Context): File {
        val dir = context.getExternalFilesDir("recordings")
            ?: context.filesDir // 외부 저장소 없으면 내부 저장소 사용

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val fileName = "record_$timestamp.mp4" // mp4 형식

        return File(dir, fileName)
    }
}