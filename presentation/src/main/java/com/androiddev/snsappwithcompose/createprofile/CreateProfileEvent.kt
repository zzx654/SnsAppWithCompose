package com.androiddev.snsappwithcompose.createprofile

import com.androiddev.snsappwithcompose.auth.signup.EmailSignUpEvent

sealed class CreateProfileEvent {
    object uploadImage: CreateProfileEvent()
    data class TypeNickname(val nickname : String) : CreateProfileEvent()
}