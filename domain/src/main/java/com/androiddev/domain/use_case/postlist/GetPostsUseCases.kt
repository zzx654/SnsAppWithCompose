package com.androiddev.domain.use_case.postlist

import javax.inject.Inject


data class GetPostsUseCases @Inject constructor(
    val getNearbyPosts: GetNearbyPosts,
    val getRecentPosts: GetRecentPosts,
    val getUserPosts: GetUserPosts,
    val getPopularPosts: GetPopularPosts
)