package com.androiddev.domain.use_case.tag

data class TagUseCases(
    val getTags: GetTags,
    val searchTag: SearchTag,
    val toggleFavoriteTag: ToggleFavoriteTag
)