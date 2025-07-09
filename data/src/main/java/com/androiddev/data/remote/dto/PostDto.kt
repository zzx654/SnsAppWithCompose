package com.androiddev.data.remote.dto

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.util.elapsedTime
import kotlin.math.round

data class PostDto(
    val postid:Int,
    val userid:Int,
    val nickname:String?,
    val anonymous:String?,
    val profileimage:String?,
    val gender:String,
    val text:String,
    val tags:String?,
    val date:String,
    val images:String?,
    val audio:String?,
    var commentcount:Int,
    var likecount:Int,
    var isliked:Int?,
    var distance:Double?,
    val vote:String?,
    val votecount:Int?

)
fun PostDto.toPostPreview(
    postid:Int,
    userid:Int,
    nickname:String?,
    anonymous:String?,
    profileimage:String?,
    gender:String,
    text:String,
    tags:String?,
    date:String,
    images:String?,
    audio:String?,
    commentcount:Int,
    likecount:Int,
    isliked:Int?,
    distance:Double?,
    vote:String?,
    votecount:Int?,
): PostPreview {
    //닉네임 거리 태그 이미지 투표 경과시간
    var nick = ""
    var anony = false
    nickname?.let {
        nick = nickname
    }
    anonymous?.let {
        if(it!="NONE") {
            nick = "익명[${it}]"
            anony = true
        }

    }
    var imageList = images?.split(',')
    return PostPreview(
        postId = postid,
        userId = userid,
        anonymous = anony,
        nickname = nick,
        profileImage = profileimage,
        gender = gender,
        text = text,
        audio = audio,
        images = imageList,
        firstImage = imageList?.get(0),
        imageSize = imageList?.size,
        tags = tags?.split('#')?.map{"#${it}"},
        date = date,
        elapsedTime = elapsedTime(date),
        voteCount = votecount,
        commentCount = commentcount,
        likecount = likecount,
        isliked = isliked != null,
        vote = vote,
        distance = distance?.let{ round(it).toInt()}
    )
}