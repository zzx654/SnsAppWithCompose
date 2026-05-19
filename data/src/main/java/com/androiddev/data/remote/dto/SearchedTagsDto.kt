package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SearchedTags
import com.androiddev.domain.model.Tag

data class SearchedTagsDto(
    val searchedTags:List<Tag>
)

fun SearchedTagsDto.toSearchTags(
): SearchedTags {
    return SearchedTags(searchedTags = searchedTags)
}