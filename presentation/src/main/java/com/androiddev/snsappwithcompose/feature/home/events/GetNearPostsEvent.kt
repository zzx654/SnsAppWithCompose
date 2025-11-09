package com.androiddev.snsappwithcompose.feature.home.events

sealed class GetNearPostsEvent: GetPostsEvent() {

    data class SetDistance(val distance: Int): GetNearPostsEvent()

}