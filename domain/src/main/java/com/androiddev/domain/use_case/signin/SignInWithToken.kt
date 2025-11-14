package com.androiddev.domain.use_case.signin

import com.androiddev.domain.model.SigninWithTokenResult
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithToken @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(): Flow<Resource<SigninWithTokenResult>> = repository.signInWithToken()
}