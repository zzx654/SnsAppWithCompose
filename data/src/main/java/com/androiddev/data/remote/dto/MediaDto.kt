package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Media

data class MediaDto(
    val id: Int,
    val url: String,
    val type: String,
    val thumbnailurl: String?
)
fun MediaDto.toMedia(
) = Media(id = id,url = url,type= type, thumbnailUrl = thumbnailurl)