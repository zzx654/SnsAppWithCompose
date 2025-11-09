package com.androiddev.snsappwithcompose.feature.home.tags

import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.Tag

data class GetTagsState(
    val isLoading: Boolean = false,
    val favoriteTags: List<Tag> = emptyList(),
    val popularTags: List<Tag> = emptyList(),
    val searchedTags: List<Tag> = emptyList(),
    val error: String? = null,
)