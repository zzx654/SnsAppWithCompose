package com.androiddev.snsappwithcompose.feature.upload_post.util

import android.content.Context
import android.net.Uri

fun isVideo(context: Context, uri: Uri): Boolean {
    val type = context.contentResolver.getType(uri)
    return type?.startsWith("video") == true
}