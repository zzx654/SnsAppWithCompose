package com.androiddev.domain.model

data class Tag(
    val tagid:Int? = null,
    val tagname:String,
    val tagcount:Int?,
    val isliked:Int? = null
)