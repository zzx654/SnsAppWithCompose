package com.androiddev.data.remote.dto


import com.androiddev.data.util.formatFullUrl
import com.androiddev.domain.model.Media

data class MediaDto(
    val id: Int,
    val url: String,
    val type: String,
    val thumbnailurl: String?
)
fun MediaDto.toDomain(
) = Media(id = id,url = formatFullUrl(url),type= type, thumbnailUrl = thumbnailurl?.let {
    formatFullUrl(
        it
    )
})