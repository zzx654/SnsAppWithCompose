package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Tag
import com.androiddev.domain.model.Tags
import com.androiddev.domain.model.ToggleLikeResponse

data class TagsDto(
    val favoriteTags:List<Tag>,
    val popularTags:List<Tag>
)

fun TagsDto.toTags(
    favoriteTags:List<Tag>,
    popularTags: List<Tag> ): Tags {
    return Tags(
        favoriteTags = favoriteTags,
        popularTags = popularTags
    )
}