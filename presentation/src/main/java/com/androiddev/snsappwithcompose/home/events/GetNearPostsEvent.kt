package com.androiddev.snsappwithcompose.home.events

sealed class GetNearPostsEvent: GetPostsEvent() {

    data class SetDistance(val distance: Int): GetNearPostsEvent()

}