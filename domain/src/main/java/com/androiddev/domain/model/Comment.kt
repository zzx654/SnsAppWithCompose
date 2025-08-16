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
    val anonymous: Boolean,
    val nickname: String,
    val profileImage: String?,
    val replyCount: Int=0,
    val likeCount: Int,
    val score:Int=0,
    val commentLiked: Int
)