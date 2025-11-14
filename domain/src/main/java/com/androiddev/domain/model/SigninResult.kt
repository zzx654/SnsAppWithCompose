package com.androiddev.domain.model

data class SigninResult(
    val isMember: Boolean,
    val profileWritten: Boolean,
    val userId: Int?,
    val token: String,
)