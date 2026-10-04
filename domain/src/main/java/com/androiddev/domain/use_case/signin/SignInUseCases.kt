package com.androiddev.domain.use_case.signin

import com.androiddev.domain.use_case.validation.ValidateEmail
import com.androiddev.domain.use_case.validation.ValidatePassword
import javax.inject.Inject

data class SignInUseCases @Inject constructor(
    val resetSocialSignIn: ResetSocialSignIn,
    val socialSignIn: SocialSignIn,
    val emailSignIn: EmailSignIn,
    val signInWithToken: SignInWithToken,
    val validateEmail: ValidateEmail,
    val validatePassword: ValidatePassword
)