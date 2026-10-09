package com.androiddev.data.remote.dto

import com.androiddev.domain.model.TokenResult

data class TokenResultDto (
    val token: String
)
fun TokenResultDto.toDomain(
): TokenResult = TokenResult(token)