package com.androiddev.domain.model

data class SearchTagResponse(
    val isTokenValid: Boolean,
    val tags: List<Tag>
)
