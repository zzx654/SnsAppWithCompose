package com.androiddev.data.remote.dto

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.Post

data class GetPostsResponseDto(
    val posts:List<Post>,
    val isTokenValid:Boolean,
    val resultCode: Int
)
fun GetPostsResponseDto.toGetPostsResponse(
    posts: List<Post>,
    isTokenValid: Boolean): GetPostsResponse {
    return GetPostsResponse( posts = posts, isTokenValid = isTokenValid)
}
