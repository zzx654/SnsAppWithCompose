package com.androiddev.data.remote.dto


import com.androiddev.domain.model.Post

data class PostsDto (
    val posts:List<PostDto>
)
fun PostsDto.toPosts(): List<Post> {
    return posts.map { it.toDomain() }
}