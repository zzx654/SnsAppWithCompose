package com.androiddev.domain.model

import android.graphics.Bitmap
import android.net.Uri

data class UploadPostParam(
    val postId: Int? = null,
    val text: String,
    val tags: List<String> = emptyList(),
    val mediaItems: List<MediaParam> = emptyList(), // 로컬 Uri/Path 정보
    val audioPath: String? = null,
    val deleteAudio: String? = null,
    val deletedVisualMedia: List<String> = emptyList(),
    val isAnonymous: Boolean = false,
    val isLocationEnabled: Boolean = false,
    val voteOptions: List<String> = emptyList()
)

data class MediaParam(
    val uri: Uri? = null,
    val remotePath:String? = null,
    val type: MediaType,
    val remoteThumbnailPath:String? = null,
    val isNew:Boolean = false
)