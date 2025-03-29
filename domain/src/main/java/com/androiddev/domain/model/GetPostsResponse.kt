package com.androiddev.domain.model

data class GetPostsResponse(
    val posts:List<Post>,
    val isTokenValid:Boolean,
)