package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Tag
import com.androiddev.domain.model.Tags

data class TagsDto(
    val favoriteTags:List<Tag>,
    val popularTags:List<Tag>
)

fun TagsDto.toTags(
): Tags {
    return Tags(
        favoriteTags = favoriteTags,
        popularTags = popularTags
    )
}