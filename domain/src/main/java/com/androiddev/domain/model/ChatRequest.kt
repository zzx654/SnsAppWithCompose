package com.androiddev.domain.model

data class ChatRequest (
    val id: Long,
    val postId: Long?,
    val commentId: Long?,
    val createdAt: String
)