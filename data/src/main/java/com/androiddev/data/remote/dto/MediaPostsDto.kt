package com.androiddev.data.remote.dto

import com.androiddev.domain.model.MediaPosts

data class MediaPostsDto(
    val mediaPosts:List<MediaPostDto>
)

fun MediaPostsDto.toMediaPosts():MediaPosts {
    return MediaPosts(mediaPosts.map{
        it.toMediaPost()
    })
}