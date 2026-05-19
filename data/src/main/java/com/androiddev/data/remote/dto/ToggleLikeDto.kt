package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleLikeResult

data class ToggleLikeDto(
    val isLiked:Boolean
)
fun ToggleLikeDto.toToggleLikeResult(
): ToggleLikeResult {
    return ToggleLikeResult(isLiked = isLiked )
}