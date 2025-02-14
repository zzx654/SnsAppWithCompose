package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninWithTokenResponse

data class SigninWithTokenResponseDto(
    val signInResult: Boolean,
    val profileWritten: Boolean,
    val resultCode: Int
)
fun SigninWithTokenResponseDto.toSigninWithTokenResponse(
    signInResult: Boolean,
    profileWritten: Boolean) : SigninWithTokenResponse {
    return SigninWithTokenResponse(signInResult = signInResult, profileWritten = profileWritten)
}