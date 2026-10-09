package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ToggleFollowResult

data class ToggleFollowDto(
    val isFollowing:Boolean
)
fun ToggleFollowDto.toDomain(
): ToggleFollowResult {
    return ToggleFollowResult(isFollowing = isFollowing )
}