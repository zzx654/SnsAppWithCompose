package com.androiddev.data.remote.dto

import com.androiddev.data.util.formatFullUrl
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
fun UserDto.toDomain(

): User{
    return User(
        userId = userid,
        nickname = nickname,
        gender = gender,
        profileImage = profileimage?.let { formatFullUrl(it) },
        following = following,
        followerCount = followercount?:0,
        postCount = postcount
    )
}