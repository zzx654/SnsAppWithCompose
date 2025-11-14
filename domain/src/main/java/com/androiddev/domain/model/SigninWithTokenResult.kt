package com.androiddev.domain.model

data class SigninWithTokenResult (
    val signInResult:Boolean,
    val profileWritten:Boolean,
    val userId: Int?,
)