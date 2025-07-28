package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleLikeResponse


data class ToggleLikeResponseDto(
    val isLiked:Boolean,
    val isTokenValid:Boolean,
    val resultCode: Int
)
fun ToggleLikeResponseDto.toToggleLikeResponse(
    isLiked:Boolean,
    isTokenValid: Boolean): ToggleLikeResponse {
    return ToggleLikeResponse(
        isLiked = isLiked,
        isTokenValid = isTokenValid
    )
}