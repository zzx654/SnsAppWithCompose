package com.androiddev.snsappwithcompose.feature.auth.signin


sealed class SignInEvent {
    data class SocialSignIn(val platform: String,val account: String): SignInEvent()
    object EmailSignIn: SignInEvent()
    data class TypeAccount(val account : String) : SignInEvent()
    data class TypePwd(val password : String) : SignInEvent()

}