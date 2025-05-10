package com.androiddev.domain.model

data class GetPostsResponse(
    val posts:List<PostPreview>,
    val isTokenValid:Boolean,
)