package com.androiddev.snsappwithcompose.auth.signup

sealed class AuthPhoneEvent {
    data class TypePhoneNumber(val phoneNumber: String) : AuthPhoneEvent()
    object RequestAuthCode : AuthPhoneEvent()
    data class TypeAuthCode(val authCode : String) : AuthPhoneEvent()
    data class AuthenticateCode(val platform: String,val account: String?) : AuthPhoneEvent()
}