package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Comments

data class CommentsDto(
    val comments:List<CommentDto>
)
fun CommentsDto.toComments(
): Comments {
    return Comments(
        comments = comments.map{
            it.toComment(
            )
        }
    )

}
