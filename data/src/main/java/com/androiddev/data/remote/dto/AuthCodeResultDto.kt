package com.androiddev.data.remote.dto

import com.androiddev.domain.model.AuthCodeResult

data class AuthCodeResultDto(
    val isCorrect:Boolean
)
fun AuthCodeResultDto.toDomain(
): AuthCodeResult {
    return AuthCodeResult(isCorrect = isCorrect)
}