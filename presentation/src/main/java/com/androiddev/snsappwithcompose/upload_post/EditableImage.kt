package com.androiddev.snsappwithcompose.upload_post

import android.net.Uri

data class EditableImage(
    val uri: Uri? = null,           // 새 이미지일 경우 사용
    val remotePath: String? = null, // 서버에서 내려준 이미지 URL
    val isNew: Boolean = false      // 새 이미지인지 여부
)