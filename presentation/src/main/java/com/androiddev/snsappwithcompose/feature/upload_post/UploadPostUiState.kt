package com.androiddev.snsappwithcompose.feature.upload_post

import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem

data class UploadPostUiState(
    val contentText: String = "",
    val isAnonymous: Boolean = false,
    val selectedMediaItems: List<MediaItem> = emptyList(),
    val isLocationOn: Boolean = false,
    val isLoading: Boolean = false
)