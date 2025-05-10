package com.androiddev.snsappwithcompose.home.nearposts

import com.androiddev.snsappwithcompose.createprofile.CreateProfileEvent

sealed class GetNearPostsEvent {
    object RefreshNearPosts: GetNearPostsEvent()
    object LoadNextPosts: GetNearPostsEvent()
    data class SetDistance(val distance: Int): GetNearPostsEvent()
    //data class PermissionChecked(val granted: Boolean): GetNearPostsEvent()
}