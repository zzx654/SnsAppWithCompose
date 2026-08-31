package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Comment

data class CommentsDto(
    val comments:List<CommentDto>
)
fun CommentsDto.toDomain(
): List<Comment> = comments.map{ it.toDomain() }
