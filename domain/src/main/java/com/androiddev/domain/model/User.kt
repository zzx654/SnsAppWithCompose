package com.androiddev.domain.model

data class User(
    val userId:Int,
    val nickname:String,
    val gender:String,
    val profileImage:String?,
    val following:Int,
    val followerCount:Int,
    val postCount:Int? = null
)