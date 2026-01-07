package com.androiddev.data.remote.dto

import com.androiddev.domain.model.User

data class UserDto(
    val userid:Int,
    val nickname:String,
    val gender:String,
    val profileimage:String?,
    val following:Int,
    val followercount:Int,
)
fun UserDto.toUser(
    userId:Int,
    nickname:String,
    gender:String,
    profileImage:String?,
    following:Int,
    followerCount:Int,
): User{
    return User(
        userId = userid,
        nickname = nickname,
        gender = gender,
        profileImage = profileimage,
        following = following,
        followerCount = followercount
    )
}