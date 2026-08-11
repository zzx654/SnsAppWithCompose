package com.androiddev.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Post(
    val postId:Int,
    val userId:Int,
    val anonymousNickname:String?,
    val nickname:String,
    val profileImage:String?,
    val gender:String,
    val date:String,
    val text:String,
    val location:Double?,
    val tags:List<String>?,
    val media:List<Media>,
    var commentCount:Int,
    var likecount:Int,
    var isliked:Boolean,
    val popularityScore:Double,
    var distance:Int?,
    val vote:String?,
    val voteCount:Int?,
    val elapsedTime:String,
): Parcelable