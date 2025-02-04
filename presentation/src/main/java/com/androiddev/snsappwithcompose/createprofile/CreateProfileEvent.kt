package com.androiddev.snsappwithcompose.createprofile


sealed class CreateProfileEvent {
    object uploadImage: CreateProfileEvent()
    data class TypeNickname(val nickname: String): CreateProfileEvent()
    data class SetBirthYear(val birthYear: Int): CreateProfileEvent()
    data class SetGender(val gender: String): CreateProfileEvent()
}