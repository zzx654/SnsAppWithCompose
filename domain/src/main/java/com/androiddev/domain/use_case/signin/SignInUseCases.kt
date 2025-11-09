package com.androiddev.domain.use_case.signin

data class SignInUseCases(
    val socialSignIn: SocialSignIn,
    val emailSignIn: EmailSignIn,
    val signInWithToken: SignInWithToken
)