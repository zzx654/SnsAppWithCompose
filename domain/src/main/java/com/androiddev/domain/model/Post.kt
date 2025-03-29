package com.androiddev.domain.model

data class Post(
    val postid:Int,
    val userid:Int,
    val nickname:String,
    val anonymous:String?,
    val profileimage:String?,
    val gender:String?,
    val text:String,
    val tags:String?,
    val date:String,
    val images:String,
    val audio:String,
    var commentcount:Int,
    var likecount:Int,
    var isliked:Int?,
    var distance:Double?,
    val vote:String,
)