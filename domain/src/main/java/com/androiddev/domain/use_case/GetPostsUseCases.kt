package com.androiddev.domain.use_case

data class GetPostsUseCases(
    val getNearPosts: GetNearPosts,
    val getSelectedPost: GetSelectedPost
)