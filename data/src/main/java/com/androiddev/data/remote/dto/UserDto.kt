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

): User{
    return User(
        userId = userid,
        nickname = nickname,
        gender = gender,
        profileImage = profileimage,
        following = following,
        followerCount = followercount?:0,
        postCount = postcount
    )
}