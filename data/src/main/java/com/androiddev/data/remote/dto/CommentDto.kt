package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Comment
import com.androiddev.domain.util.elapsedTime

data class CommentDto (
    val postid: Int,
    val commentid: Int,
    val userid: Int,
    val text: String,
    val ref: Int,
    val date:String,
    val depth: Int,
    val anonymous: String?,
    val nickname: String?,
    val gender: String,
    val profileimage: String?,
    val replycount: Int,
    val likecount: Int,
    val score: Int = 0,
    val commentliked: Int
)
fun CommentDto.toComment(
): Comment {
    return Comment(
        postId = postid,
        commentId = commentid,
        userId = userid,
        text = text,
        ref = ref,
        date = date,
        depth = depth,
        anonymousNickname = anonymous,
        nickname = nickname?:"",
        gender = gender,
        profileImage = profileimage,
        replyCount = replycount,
        likeCount = likecount,
        score = score,
        commentLiked = commentliked,
        elapsedTime = elapsedTime(date)
    )
}