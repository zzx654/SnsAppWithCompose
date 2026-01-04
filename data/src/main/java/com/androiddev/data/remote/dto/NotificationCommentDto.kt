package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationComment

data class NotificationCommentDto(
    val comment:CommentDto,
    val reply:CommentDto?
)
fun NotificationCommentDto.toNotificationComment(
    comment:CommentDto,
    reply:CommentDto?
): NotificationComment {
    return NotificationComment(
        comment = comment.toComment(
            postid = comment.postid,
            commentid = comment.commentid,
            userid = comment.userid,
            text = comment.text,
            ref = comment.ref,
            date = comment.date,
            depth = comment.depth,
            anonymous = comment.anonymous ,
            nickname = comment.nickname,
            profileimage = comment.profileimage,
            gender = comment.gender,
            replycount = comment.replycount,
            likecount = comment.likecount,
            score = comment.score,
            commentliked = comment.commentliked,
        ),
        reply = reply?.toComment(
            postid = reply.postid,
            commentid = reply.commentid,
            userid = reply.userid,
            text = reply.text,
            ref = reply.ref,
            date = reply.date,
            depth = reply.depth,
            anonymous = reply.anonymous ,
            nickname = reply.nickname,
            profileimage = reply.profileimage,
            gender = reply.gender,
            replycount = reply.replycount,
            likecount = reply.likecount,
            score = reply.score,
            commentliked = reply.commentliked,
        )
    )
}