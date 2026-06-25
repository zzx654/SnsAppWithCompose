package com.androiddev.domain.model

data class MediaPostQuery(
    val userId:Int,
    val type:String = MediaType.IMAGE.name,
    val latitude:Double? = null,
    val longitude:Double? = null
)