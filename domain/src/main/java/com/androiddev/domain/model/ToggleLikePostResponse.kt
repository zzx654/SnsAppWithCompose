package com.androiddev.domain.model

data class ToggleLikePostResponse(
    val isLiked: Boolean,
    val isTokenValid: Boolean,
)