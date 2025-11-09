package com.androiddev.snsappwithcompose.feature.home.events

sealed class GetNewPostsEvent: GetPostsEvent() {

    data class SetDistance(val distance: Int): GetNearPostsEvent()

}