package com.androiddev.data.remote.dto

import com.androiddev.domain.model.ValidationResult

data class ValidationResultDto (
    val isValid:Boolean
)
fun ValidationResultDto.toValidationResult(
): ValidationResult {
    return ValidationResult(isValid = isValid)
}