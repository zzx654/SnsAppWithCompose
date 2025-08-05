package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninResponse

data class SigninResponseDto(
    val isMember: Boolean,
    val profileWritten: Boolean,
    val userId: Int?,
    val token: String,
    val resultCode: Int
)
fun SigninResponseDto.toSigninResponse(
    isMember: Boolean,
    profileWritten: Boolean,
    userId: Int? = null,
    token: String): SigninResponse {
    return SigninResponse(isMember = isMember, profileWritten = profileWritten,userId = userId, token = token )
}

