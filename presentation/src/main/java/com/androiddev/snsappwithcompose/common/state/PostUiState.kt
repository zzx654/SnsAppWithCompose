package com.androiddev.snsappwithcompose.common.state

import com.androiddev.domain.model.PostPreview

data class PostUiState(
    val post: PostPreview,
    val imageUrls: List<String>,
    val hasVideo: Boolean,
    val hasAudio: Boolean,
    val displayUserName: String
)