package com.androiddev.snsappwithcompose.feature.auth.signup.authphone

sealed class AuthPhoneEvent {
    data class TypePhoneNumber(val phoneNumber: String) : AuthPhoneEvent()
    object RequestAuthCode : AuthPhoneEvent()
    data class TypeAuthCode(val authCode : String) : AuthPhoneEvent()
    object AuthenticateCode : AuthPhoneEvent()
}