package com.androiddev.snsappwithcompose.feature.createprofile.event

import android.net.Uri


sealed class CreateProfileEvent {
    data class TypeNickname(val nickname: String): CreateProfileEvent()
    data class SetBirthYear(val birthYear: Int): CreateProfileEvent()
    data class SetGender(val gender: String): CreateProfileEvent()
    object ShowProfileImageOptions: CreateProfileEvent()
    object ShowBirthYearOptions: CreateProfileEvent()
    data class ShowCreateProfileAlert(val imageUri: Uri?): CreateProfileEvent()
}