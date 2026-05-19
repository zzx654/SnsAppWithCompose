package com.androiddev.data.remote.dto

import com.androiddev.domain.model.SigninWithTokenResult
import kotlin.math.sign

data class SigninWithTokenResultDto(
    val signInResult:Boolean,
    val profileWritten:Boolean,
    val userId: Int?,
)
fun SigninWithTokenResultDto.toSigninWithTokenResult(
):SigninWithTokenResult {
    return SigninWithTokenResult(
        signInResult = signInResult,
        profileWritten = profileWritten,
        userId = userId
    )
}