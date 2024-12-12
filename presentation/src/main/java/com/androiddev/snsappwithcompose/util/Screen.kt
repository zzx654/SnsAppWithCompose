package com.androiddev.snsappwithcompose.util
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object SignInScreen: Screen
    @Serializable
    data class SignUpScreen(val phoneNumber: String): Screen
    @Serializable
    data class AuthPhoneScreen(val platform:String,val account:String?): Screen
    @Serializable
    data object CreateprofileScreen: Screen
    @Serializable
    data object HomeScreen: Screen
    @Serializable
    data object InitScreen: Screen

}