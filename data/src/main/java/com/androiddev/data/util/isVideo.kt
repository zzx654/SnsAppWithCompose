package com.androiddev.data.util

import android.content.Context
import android.net.Uri

fun isVideo(context: Context, uri: Uri): Boolean {
    val type = context.contentResolver.getType(uri)
    return type?.startsWith("video") == true
}