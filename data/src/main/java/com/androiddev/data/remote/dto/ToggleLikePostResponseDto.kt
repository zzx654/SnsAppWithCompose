package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleLikePostResponse


data class ToggleLikePostResponseDto(
    val isLiked:Boolean,
    val isTokenValid:Boolean,
    val resultCode: Int
)
fun ToggleLikePostResponseDto.toToggleLikePostResponse(
    isLiked:Boolean,
    isTokenValid: Boolean): ToggleLikePostResponse {
    return ToggleLikePostResponse(
        isLiked = isLiked,
        isTokenValid = isTokenValid
    )
}