package com.androiddev.data.remote.dto

import com.androiddev.domain.model.User

data class UserDto(
    val userid:Int,
    val nickname:String,
    val gender:String,
    val profileimage:String?,
    val following:Int,
    val followercount:Int?,
    val postcount:Int?
)
fun UserDto.toUser(
    userId:Int,
    nickname:String,
    gender:String,
    profileImage:String?,
    following:Int,
    followerCount:Int?,
    postCount:Int?
): User{
    return User(
        userId = userId,
        nickname = nickname,
        gender = gender,
        profileImage = profileImage,
        following = following,
        followerCount = followerCount?:0,
        postCount = postCount
    )
}