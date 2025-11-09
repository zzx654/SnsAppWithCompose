package com.androiddev.snsappwithcompose.feature.home.events

sealed class GetPostsEvent {
    object Refresh : GetPostsEvent()
    object LoadNext : GetPostsEvent()
    data class SelectPost(val postId: Int): GetPostsEvent()
}