package com.androiddev.snsappwithcompose.util
import android.net.Uri
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
    @Serializable
    data class CropScreen(val encodedUri:String): Screen

}