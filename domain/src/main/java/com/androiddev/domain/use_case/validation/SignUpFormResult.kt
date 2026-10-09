package com.androiddev.domain.use_case.validation

enum class SignUpFormResult {
    SUCCESS,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    PASSWORD_MISMATCH,
    EMPTY_AUTH_CODE
}