package com.androiddev.snsappwithcompose.home.nearposts

import com.androiddev.snsappwithcompose.createprofile.CreateProfileEvent

sealed class GetNearPostsEvent {
    data class RefreshNearPosts(val latitude: Double,val longitude: Double): GetNearPostsEvent()
    data class SetDistance(val distance: Int): GetNearPostsEvent()
}