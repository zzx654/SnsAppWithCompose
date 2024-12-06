package com.androiddev.snsappwithcompose.auth.signin

sealed class SignInEvent {
    data class socialSignIn(val platform: String,val account: String): SignInEvent()
}