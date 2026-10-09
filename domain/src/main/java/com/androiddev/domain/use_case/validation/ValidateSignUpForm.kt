package com.androiddev.domain.use_case.validation

import javax.inject.Inject

class ValidateSignUpForm @Inject constructor(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidateSignUpPassword
) {
    operator fun invoke(email: String, pw: String, repeatPw: String, authCode: String): SignUpFormResult {
        if (validateEmail(email)!=null) return SignUpFormResult.INVALID_EMAIL
        if (!validatePassword(pw)) return SignUpFormResult.INVALID_PASSWORD // 정규식 검사
        if (pw != repeatPw) return SignUpFormResult.PASSWORD_MISMATCH
        if (authCode.isBlank()) return SignUpFormResult.EMPTY_AUTH_CODE
        return SignUpFormResult.SUCCESS
    }
}