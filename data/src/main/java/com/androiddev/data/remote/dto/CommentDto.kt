package com.androiddev.data.remote.dto

import com.androiddev.data.util.formatFullUrl
import com.androiddev.domain.model.Comment

data class CommentDto (
    val postid: Long,
    val commentid: Long,
    val userid: Long,
    val text: String,
    val ref: Long,
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
fun CommentDto.toDomain(

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
        profileImage = profileimage?.let { formatFullUrl(it) },
        replyCount = replycount,
        likeCount = likecount,
        score = score,
        commentLiked = commentliked
    )
}
