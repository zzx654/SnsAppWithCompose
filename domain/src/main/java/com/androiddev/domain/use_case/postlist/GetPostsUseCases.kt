package com.androiddev.domain.use_case.postlist

data class GetPostsUseCases(
    val getNearPosts: GetNearPosts,
    val getNewPosts: GetNewPosts,
    val getPopularTagPosts: GetPopularTagPosts,
    val getNewTagPosts: GetNewTagPosts,
    val getSelectedPost: GetSelectedPost
)