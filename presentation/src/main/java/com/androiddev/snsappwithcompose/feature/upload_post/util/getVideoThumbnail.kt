package com.androiddev.snsappwithcompose.feature.upload_post.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, uri)
        retriever.getFrameAtTime(1_000_000) // 1초 지점
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}