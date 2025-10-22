package com.androiddev.domain.model

data class GetTagsResponse(
    val favoriteTags:List<Tag>,
    val popularTags:List<Tag>
)