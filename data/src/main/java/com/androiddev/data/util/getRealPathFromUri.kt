package com.androiddev.data.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.also

fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
    val contentResolver = context.contentResolver ?: return null
    // 파일생성
    val filePath = "${context.applicationInfo.dataDir}${File.separator}${System.currentTimeMillis()}"
    val file = File(filePath)
    return try {
        val inputStream: InputStream = contentResolver.openInputStream(contentUri) ?: return null
        val outputStream: OutputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        filePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}