package com.androiddev.snsappwithcompose.feature.home.user

sealed class UserEvent {
    data class TypeNickname(val nickname: String):UserEvent()
}