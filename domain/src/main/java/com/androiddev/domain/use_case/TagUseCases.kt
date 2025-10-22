package com.androiddev.domain.use_case

data class TagUseCases(
    val getTags: GetTags,
    val searchTag: SearchTag,
    val toggleFavoriteTag: ToggleFavoriteTag
)