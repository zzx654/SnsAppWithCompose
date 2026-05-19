package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleFollowResult

data class ToggleFollowDto(
    val isFollowing:Boolean
)
fun ToggleFollowDto.toToggleFollowResult(
): ToggleFollowResult {
    return ToggleFollowResult(isFollowing = isFollowing )
}