package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninResult

data class SigninResultDto (
    val isMember: Boolean,
    val profileWritten: Boolean,
    val userId: Int?,
    val token: String,
)
fun SigninResultDto.toSigninResult(
): SigninResult {
    return SigninResult(
        isMember = isMember,
        profileWritten = profileWritten,
        userId = userId,
        token = token
    )
}