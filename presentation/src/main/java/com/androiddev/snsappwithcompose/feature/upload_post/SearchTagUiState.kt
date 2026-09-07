package com.androiddev.snsappwithcompose.feature.upload_post

import com.androiddev.domain.model.Tag

data class SearchTagUiState(
    val tagText: String = "",
    val addedTags: List<String> = emptyList(),
    val searchedTags: List<Tag> = emptyList(),
    val isLoading: Boolean = false
)