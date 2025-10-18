package com.androiddev.domain.use_case

data class GetPostsUseCases(
    val getNearPosts: GetNearPosts,
    val getNewPosts: GetNewPosts,
    val getPopularTagPosts:GetPopularTagPosts,
    val getSelectedPost: GetSelectedPost
)