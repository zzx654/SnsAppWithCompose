package com.androiddev.data.util

import com.androiddev.data.BuildConfig

fun formatFullUrl(path: String): String {
    if (path.startsWith("http://") || path.startsWith("https://")) {
        return path
    }
    val baseUrl = BuildConfig.BASE_URL.removeSuffix("/")
    val cleanPath = path.removePrefix("/")
    return "$baseUrl/$cleanPath"
}