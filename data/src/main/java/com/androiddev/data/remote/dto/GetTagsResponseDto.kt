package com.androiddev.data.remote.dto

import com.androiddev.domain.model.GetTagsResponse
import com.androiddev.domain.model.Tag

data class GetTagsResponseDto(
    val isTokenValid:Boolean,
    val resultCode: Int,
    val favoriteTags:List<Tag>,
    val popularTags:List<Tag>
)
fun GetTagsResponseDto.toGetTagsResponse(
    isTokenValid: Boolean,
    favoriteTags: List<Tag>,
    popularTags: List<Tag>

): GetTagsResponse{
    return GetTagsResponse(
        favoriteTags = favoriteTags,
        popularTags = popularTags
    )
}

