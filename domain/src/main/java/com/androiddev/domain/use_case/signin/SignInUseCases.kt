package com.androiddev.domain.use_case.signin

import javax.inject.Inject

data class SignInUseCases @Inject constructor(
    val resetSocialSignIn: ResetSocialSignIn,
    val socialSignIn: SocialSignIn,
    val emailSignIn: EmailSignIn,
    val signInWithToken: SignInWithToken
)