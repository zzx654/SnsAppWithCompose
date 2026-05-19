package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Media
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.util.elapsedTime
import kotlin.math.round

data class PostDto(
    val postid:Int,
    val userid:Int,
    val nickname:String?,
    val anonymous:String?,
    val profileimage:String?,
    val location:Double?,
    val gender:String,
    val text:String,
    val tags:String?,
    val date:String,
    val media:List<MediaDto>,
    var commentcount:Int,
    var likecount:Int,
    var isliked:Int?,
    val score:Int?,
    val popularityScore:Double?,
    var distance:Double?,
    val vote:String?,
    val votecount:Int?

)
fun PostDto.toPostPreview(

): PostPreview {
    //닉네임 거리 태그 이미지 투표 경과시간

    return PostPreview(
        postId = postid,
        userId = userid,
        anonymousNickname = anonymous,
        nickname = nickname?:"",
        profileImage = profileimage,
        gender = gender,
        text = text,
        location = location,
        media = media.map { it.toMedia() },
        tags = tags?.split('#'),
        date = date,
        elapsedTime = elapsedTime(date),
        voteCount = votecount,
        commentCount = commentcount,
        likecount = likecount,
        isliked = isliked != null,
        popularityScore = popularityScore?:0.toDouble(),
        vote = vote,
        distance = distance?.let{ round(it).toInt()}
    )
}