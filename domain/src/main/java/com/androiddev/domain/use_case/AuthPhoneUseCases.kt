package com.androiddev.domain.use_case

data class AuthPhoneUseCases(
    val requestAuthCode: RequestPhoneAuthCode,
    val authenticateCode: AuthenticateCode
)