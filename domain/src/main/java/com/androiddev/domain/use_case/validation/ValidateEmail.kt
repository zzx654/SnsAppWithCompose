package com.androiddev.domain.use_case.validation


import javax.inject.Inject

class ValidateEmail @Inject constructor() {
    operator fun invoke(email: String): EmailValidationError? {
        if (email.isBlank()) {
            return EmailValidationError.EMPTY
        }
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
            return EmailValidationError.INVALID_FORMAT
        }
        return null // 에러 없음 (유효함)
    }
}
enum class EmailValidationError {
    EMPTY,
    INVALID_FORMAT
}