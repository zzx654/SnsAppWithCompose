package com.androiddev.snsappwithcompose.home.nearposts

sealed class GetNearPostsEvent {
    data class RefreshNearPosts(val latitude: Double,val longitude: Double): GetNearPostsEvent()
}