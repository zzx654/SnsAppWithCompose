package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleFollowResult

data class ToggleFollowDto(
    val isFollowing:Boolean
)
fun ToggleFollowDto.toToggleFollowResult(
    isFollowing: Boolean
): ToggleFollowResult {
    return ToggleFollowResult(isFollowing = isFollowing )
}