package com.androiddev.data.MediaItem

import android.graphics.Bitmap
import android.net.Uri
import com.androiddev.domain.model.MediaParam
import com.androiddev.domain.model.MediaType

data class MediaItem(
    val uri: Uri?=null,
    val remotePath:String? = null,
    val type: MediaType,
    val thumbnail: Bitmap? = null,
    val remoteThumbnailPath:String? = null,
    val isNew: Boolean = false
)

fun MediaItem.toParam(): MediaParam {
    return MediaParam(
        uri = this.uri,
        remotePath = this.remotePath, // 또는 localFilePath
        type = this.type,     // Domain의 MediaType
        isNew = this.isNew
    )
}
