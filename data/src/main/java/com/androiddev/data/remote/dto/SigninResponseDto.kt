package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninResponse

data class SigninResponseDto(
    val token: String,
    val resultCode: Int
)
fun SigninResponseDto.toSigninResponse(token: String) : SigninResponse {
    return SigninResponse(
        token = token
    )
}