package com.androiddev.data.remote.dto

import com.androiddev.data.util.formatFullUrl
import com.androiddev.domain.model.MediaPost
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

fun MediaPostDto.toDomain(

): MediaPost {
    return MediaPost(
        id = id,
        postId = postid,
        userId = userid,
        nickname = nickname,
        text = text,
        date = date,
        distance = distance?.let{ round(it).toInt()},
        commentCount = commentcount,
        likecount = likecount,
        url = formatFullUrl(url),
        thumbnailUrl = thumbnailurl?.let { formatFullUrl(it) },
        type = type
    )

}