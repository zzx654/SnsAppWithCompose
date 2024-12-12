package com.androiddev.domain.use_case

import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.model.SigninWithTokenResponse
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithToken @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(): Flow<Resource<SigninWithTokenResponse>> = repository.signInWithToken()
}