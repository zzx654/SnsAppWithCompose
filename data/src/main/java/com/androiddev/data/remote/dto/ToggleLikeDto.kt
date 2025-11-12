package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleLikeResult

data class ToggleLikeDto(
    val isLiked:Boolean
)
fun ToggleLikeDto.toToggleLikeResult(
    isLiked: Boolean
): ToggleLikeResult {
    return ToggleLikeResult(isLiked = isLiked )
}