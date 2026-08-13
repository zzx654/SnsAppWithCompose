package com.androiddev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val postId:Int,
    val commentId: Int?,
    val userId: Int,
    val text: String,
    val ref: Int,
    val date:String,
    val gender:String,
    val elapsedTime:String,
    val depth: Int,
    val anonymousNickname:String?,
    val nickname: String,
    val profileImage: String?,
    val replyCount: Int=0,
    val likeCount: Int,
    val score:Int=0,
    val commentLiked: Int
) {
    fun toggleLike(isLiked: Boolean): Comment {
        val newCount = if (isLiked) likeCount + 1 else (likeCount - 1).coerceAtLeast(0)
        return copy(
            commentLiked = if (isLiked) 1 else 0,
            likeCount = newCount
        )
    }
}