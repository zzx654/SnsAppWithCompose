package com.androiddev.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Media(
    val id: Int,
    val url: String,
    val type: String,
    val thumbnailUrl:String?

):Parcelable