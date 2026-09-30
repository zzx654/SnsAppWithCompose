package com.androiddev.domain.model

data class User(
    val userId:Int,
    val nickname:String,
    val gender:String,
    val profileImage:String?,
    val following:Int,
    val followerCount:Int,
    val postCount:Int? = null
) {
    fun toggleFollow(isFollowed: Boolean): User{
        val newCount = if (isFollowed) followerCount + 1 else (followerCount - 1).coerceAtLeast(0)

        return copy(
            following = if (isFollowed) 1 else 0,
            followerCount = newCount
        )

    }
}