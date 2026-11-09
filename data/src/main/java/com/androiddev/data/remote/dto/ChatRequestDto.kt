package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ChatRequest

data class ChatRequestDto (
    val chatrequestid: Long,
    val requesterid: Long,
    val postid: Long?,
    val commentid: Long?,
    val createdat: String,
    val requesternickname: String?,
    val requesterprofileimage: String?,
    val requestergender: String?,
    val postcontent: String?,
    val commentcontent: String?
)

fun ChatRequestDto.toDomain(
) = ChatRequest(
    id = chatrequestid,
    requesterId = requesterid,
    postId = postid,
    commentId = commentid,
    createdAt = createdat,
    requesterNickname = requesternickname,
    requesterProfileimage = requesterprofileimage,
    requesterGender = requestergender,
    postContent = postcontent,
    commentContent = commentcontent
)