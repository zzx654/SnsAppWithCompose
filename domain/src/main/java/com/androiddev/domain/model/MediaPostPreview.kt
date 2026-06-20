package com.androiddev.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MediaPostPreview (
    val id: Int,
    val postId:Int,
    val userId:Int,
    val nickname:String,
    val text: String,
    val date:String,
    val elapsedTime:String,
    var distance:Int?,
    var commentCount:Int,
    var likecount:Int,
    val url: String,
    val thumbnailUrl:String?,
    val type: String

):Parcelable
