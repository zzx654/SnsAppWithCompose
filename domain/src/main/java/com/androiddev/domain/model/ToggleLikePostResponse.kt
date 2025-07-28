package com.androiddev.domain.model

data class ToggleLikeResponse(
    val isLiked: Boolean,
    val isTokenValid: Boolean,
)