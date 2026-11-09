package com.androiddev.domain.model

data class ChatRequest (
    val id: Long,
    val requesterId: Long,
    val postId: Long?,
    val commentId: Long?,
    val createdAt: String,
    val requesterNickname: String?,
    val requesterProfileimage: String?,
    val requesterGender: String?,
    val postContent: String?,
    val commentContent: String?
)