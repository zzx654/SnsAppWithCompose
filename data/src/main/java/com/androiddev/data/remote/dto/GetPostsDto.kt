package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Posts

data class GetPostsDto (
    val posts:List<PostDto>
)
fun GetPostsDto.toPosts():Posts {
    return Posts(
        posts = posts.map{
            it.toPostPreview()
        }
    )
}