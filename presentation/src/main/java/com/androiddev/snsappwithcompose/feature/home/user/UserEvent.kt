package com.androiddev.snsappwithcompose.feature.home.user

sealed class UserEvent {
    data class TypeNickname(val nickname: String): UserEvent()
    data object LoadNext: UserEvent()
    data class ToggleFollowUser(val userId: Int): UserEvent()
    data class SelectUser(val userId: Int): UserEvent()
    data class GetUserInfo(val userId: Int): UserEvent()
}