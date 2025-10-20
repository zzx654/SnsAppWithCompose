package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.model.Tag

data class SearchTagResponseDto(
    val isTokenValid:Boolean,
    val resultCode: Int,
    val searchedTags:List<Tag>
)
fun SearchTagResponseDto.toGetTagsResponse(
    isTokenValid: Boolean,
    searchedTags: List<Tag>
): SearchTagResponse {
    return SearchTagResponse(
        isTokenValid = isTokenValid,
        tags = searchedTags
    )
}