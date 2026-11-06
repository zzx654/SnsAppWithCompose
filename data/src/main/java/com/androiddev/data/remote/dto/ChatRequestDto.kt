package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ChatRequest

data class ChatRequestDto (
    val id: Long,
    val postId: Long?,
    val commentId: Long?,
    val createdAt: String
)

fun ChatRequestDto.toDomain(
) = ChatRequest(
    id = id,
    postId = postId,
    commentId = commentId,
    createdAt = createdAt
)