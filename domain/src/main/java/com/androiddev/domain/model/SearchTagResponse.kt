package com.androiddev.domain.model

data class SearchTagResponse(
    val resultCode: Int,
    val tags: List<TagInfo>
)