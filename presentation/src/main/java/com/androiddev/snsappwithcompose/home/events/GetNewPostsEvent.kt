package com.androiddev.snsappwithcompose.home.events

sealed class GetNewPostsEvent: GetPostsEvent() {

    data class SetDistance(val distance: Int): GetNearPostsEvent()

}