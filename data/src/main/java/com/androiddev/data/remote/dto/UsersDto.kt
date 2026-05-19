package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Users

data class UsersDto(
    val users:List<UserDto>
)
fun UsersDto.toUsers(
): Users {
    return Users(
        users = users.map {
            it.toUser(
            )
        }
    )
}