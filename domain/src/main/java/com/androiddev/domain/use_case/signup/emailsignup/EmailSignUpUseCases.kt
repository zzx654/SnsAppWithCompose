package com.androiddev.domain.use_case.signup.emailsignup

import com.androiddev.domain.use_case.validation.ValidateEmail
import com.androiddev.domain.use_case.validation.ValidateSignUpForm
import com.androiddev.domain.use_case.validation.ValidateSignUpPassword
import javax.inject.Inject

data class EmailSignUpUseCases @Inject constructor(
    val validateSignUpPassword: ValidateSignUpPassword,
    val validateEmail: ValidateEmail,
    val validateSignUpForm:ValidateSignUpForm,
    val requestAuthCode: RequestEmailAuthCode,
    val emailSignUp: EmailSignUp
)