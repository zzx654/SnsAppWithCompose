package com.androiddev.domain.model

sealed interface PostListType {
    data class Nearby(val radiusKm:Int = 5) : PostListType
    data object Recent: PostListType
    //data object Popular : PostListType
    //data class TagPopular(val tag: String) : PostListType
    data class TagRecent(val tagId: Int) : PostListType
    data class User(val userId: Int) : PostListType
    //data object Bookmark : PostListType
}