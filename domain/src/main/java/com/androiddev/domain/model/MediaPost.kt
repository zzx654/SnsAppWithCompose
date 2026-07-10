package com.androiddev.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MediaPost (
    val id: Int,
    val postId:Int,
    val userId:Int,
    val nickname:String,
    val text: String,
    val date:String,
    var distance:Int?,
    var commentCount:Int,
    var likecount:Int,
    val url: String,
    val thumbnailUrl:String?,
    val type: String

):Parcelable {
    val previewUrl: String
        get() = if (type == MediaType.IMAGE.name) {
            url.orEmpty()
        } else {
            thumbnailUrl.orEmpty()
        }

}
