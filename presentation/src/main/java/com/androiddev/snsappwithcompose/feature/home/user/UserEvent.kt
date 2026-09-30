package com.androiddev.snsappwithcompose.feature.home.user

import com.androiddev.domain.model.User

sealed class UserEvent {
    data class TypeNickname(val nickname: String): UserEvent()
    data class ToggleFollowUser(val user: User): UserEvent()
    data class SelectUser(val userId: Int): UserEvent()
    data class GetUserInfo(val userId: Int): UserEvent()
}