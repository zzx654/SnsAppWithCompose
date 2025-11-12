package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Comments

data class CommentsDto(
    val comments:List<CommentDto>
)
fun CommentsDto.toComments(
    comments:List<CommentDto>
): Comments {
    return Comments(
        comments = comments.map{
            it.toComment(
                postid = it.postid,
                commentid = it.commentid,
                userid = it.userid,
                text = it.text,
                ref = it.ref,
                date = it.date,
                depth = it.depth,
                anonymous = it.anonymous,
                nickname = it.nickname,
                gender = it.gender,
                profileimage = it.profileimage,
                replycount = it.replycount,
                likecount = it.likecount,
                commentliked = it.commentliked,
                score = it.score
            )
        }
    )

}