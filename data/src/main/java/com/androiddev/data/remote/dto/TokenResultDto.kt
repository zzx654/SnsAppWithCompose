package com.androiddev.data.remote.dto

import com.androiddev.domain.model.TokenResult

data class TokenResultDto (
    val token: String
)
fun TokenResultDto.toTokenResult(
): TokenResult = TokenResult(token)