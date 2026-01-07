package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Users

data class UsersDto(
    val users:List<UserDto>
)
fun UsersDto.toUsers(
    users:List<UserDto>
): Users {
    return Users(
        users = users.map {
            it.toUser(
                userId = it.userid,
                nickname = it.nickname,
                gender = it.gender,
                profileImage = it.profileimage,
                following = it.following,
                followerCount = it.followercount
            )
        }
    )
}