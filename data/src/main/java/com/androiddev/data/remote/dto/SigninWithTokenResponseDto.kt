package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninWithTokenResponse

data class SigninWithTokenResponseDto(
    val signInResult: Boolean,
    val profileWritten: Boolean,
    val userId: Int?,
    val resultCode: Int
)
fun SigninWithTokenResponseDto.toSigninWithTokenResponse(
    signInResult: Boolean,
    profileWritten: Boolean,
    userId: Int? = null,
) : SigninWithTokenResponse {
    return SigninWithTokenResponse(signInResult = signInResult,userId = userId,profileWritten = profileWritten)
}