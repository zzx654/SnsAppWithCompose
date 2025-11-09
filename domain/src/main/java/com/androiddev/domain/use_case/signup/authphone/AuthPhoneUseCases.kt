package com.androiddev.domain.use_case.signup.authphone

data class AuthPhoneUseCases(
    val requestAuthCode: RequestPhoneAuthCode,
    val authenticateCode: AuthenticateCode
)