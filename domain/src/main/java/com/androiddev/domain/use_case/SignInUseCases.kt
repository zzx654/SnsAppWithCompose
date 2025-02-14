package com.androiddev.domain.use_case

data class SignInUseCases(
    val socialSignIn: SocialSignIn,
    val emailSignIn: EmailSignIn,
    val signInWithToken: SignInWithToken
)