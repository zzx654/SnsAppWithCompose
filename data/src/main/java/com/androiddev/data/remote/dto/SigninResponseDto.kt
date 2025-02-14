package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninResponse

data class SigninResponseDto(
    val isMember: Boolean,
    val profileWritten: Boolean,
    val token: String,
    val resultCode: Int
)
fun SigninResponseDto.toSigninResponse(
    isMember: Boolean,
    profileWritten: Boolean,
    token: String): SigninResponse {
    return SigninResponse(isMember = isMember, profileWritten = profileWritten, token = token )
}

