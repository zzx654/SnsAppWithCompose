package com.androiddev.domain.model

data class ChatRequestExtra(
    val postId: Long? = null,
    val commentId: Long? = null,
    val requesterId: Long? = null
)
data class ChatRequest (
    val id: Long,
    val requesterId: Long?,
    val postId: Long?,
    val commentId: Long?,
    val createdAt: String,
    val requesterNickname: String?,
    val requesterProfileimage: String?,
    val requesterGender: String?,
    val content: String?
)