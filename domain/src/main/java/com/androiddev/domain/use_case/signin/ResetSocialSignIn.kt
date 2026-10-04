package com.androiddev.domain.use_case.signin

import com.androiddev.domain.repository.signin.SigninRepository
import javax.inject.Inject

class ResetSocialSignIn @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.resetSocialSignIn()
}