package com.androiddev.domain.model

data class Comment(
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
    val replyCount: Int,
    val likeCount: Int,
    val score:Int=0,
    val commentLiked: Int
)