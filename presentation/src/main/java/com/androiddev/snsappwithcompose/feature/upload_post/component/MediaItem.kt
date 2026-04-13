package com.androiddev.snsappwithcompose.feature.upload_post.component

import android.graphics.Bitmap
import android.net.Uri

data class MediaItem(
    val uri: Uri?=null,
    val remotePath:String? = null,
    val type: MediaType,
    val thumbnail: Bitmap? = null,
    val isNew: Boolean = false
)
enum class MediaType {
    IMAGE,
    VIDEO
}