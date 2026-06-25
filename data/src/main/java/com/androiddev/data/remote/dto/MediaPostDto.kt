package com.androiddev.data.remote.dto

import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.util.elapsedTime
import kotlin.math.round

data class MediaPostDto (
    val id: Int,
    val userid: Int,
    val postid: Int,
    val type: String,
    val url: String,
    val thumbnailurl: String?,
    val text: String,
    val date: String,
    val nickname: String,
    val commentcount: Int,
    val likecount: Int,
    val distance: Double?
)

fun MediaPostDto.toMediaPost(

): MediaPost {
    return MediaPost(
        id = id,
        postId = postid,
        userId = userid,
        nickname = nickname,
        text = text,
        date = date,
        elapsedTime = elapsedTime(date),
        distance = distance?.let{ round(it).toInt()},
        commentCount = commentcount,
        likecount = likecount,
        url = url,
        thumbnailUrl = thumbnailurl,
        type = type
    )

}