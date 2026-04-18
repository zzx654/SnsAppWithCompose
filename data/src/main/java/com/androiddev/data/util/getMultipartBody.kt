package com.androiddev.data.util

import android.content.Context
import android.net.Uri
import com.androiddev.domain.model.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File


fun getMultipartBody(uri: Uri?=null, path:String? = null,context: Context,type: MediaType): MultipartBody.Part {
    var file: File
    var mimeType: String

    if (path != null) {
        file = File(path)

        mimeType = when (type) {
            MediaType.IMAGE -> "image/jpeg"
            MediaType.VIDEO -> "video/mp4"
            MediaType.AUDIO -> "audio/*"
        }

    } else if (uri != null) {


        val realMime = context.contentResolver.getType(uri)

        mimeType = realMime ?: when (type) {
            MediaType.IMAGE -> "image/jpeg"
            MediaType.VIDEO -> "video/mp4"
            MediaType.AUDIO -> "audio/mpeg"
        }

        val extension = when {
            mimeType.contains("jpeg") -> ".jpg"
            mimeType.contains("png") -> ".png"
            mimeType.contains("mp4") -> ".mp4"
            mimeType.contains("quicktime") -> ".mov"
            mimeType.contains("matroska") -> ".mkv"
            mimeType.contains("mpeg") -> ".mp3"
            else -> ""
        }

        file = File(
            context.cacheDir,
            "upload_${System.currentTimeMillis()}$extension"
        )

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

    } else {
        throw IllegalArgumentException("uri 또는 path 중 하나는 반드시 필요")
    }

    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "media",
        file.name,
        requestBody
    )
}