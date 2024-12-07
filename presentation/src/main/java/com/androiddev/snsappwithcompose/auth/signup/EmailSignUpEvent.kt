package com.androiddev.snsappwithcompose.auth.signup

sealed class EmailSignUpEvent {
    data class TypeEmail(val email: String) : EmailSignUpEvent()
    object RequestAuthCode: EmailSignUpEvent()
    data class TypeAuthCode(val authCode : String) : EmailSignUpEvent()
    data class TypePwd(val password : String) : EmailSignUpEvent()
    data class TypeRepeatPwd(val repeatPwd : String) : EmailSignUpEvent()

}
