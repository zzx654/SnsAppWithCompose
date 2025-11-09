package com.androiddev.domain.use_case.signup.emailsignup

data class EmailSignUpUseCases(
    val requestAuthCode: RequestEmailAuthCode,
    val emailSignUp: EmailSignUp
)