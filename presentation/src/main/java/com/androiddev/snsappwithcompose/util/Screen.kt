package com.androiddev.snsappwithcompose.util
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object LoginScreen: Screen
    @Serializable
    data class SignUpScreen(val phoneNumber: String): Screen
    @Serializable
    data class AuthPhoneScreen(val platform:String?,val id:String?): Screen

}