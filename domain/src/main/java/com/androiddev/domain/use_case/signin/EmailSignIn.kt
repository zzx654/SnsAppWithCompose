package com.androiddev.domain.use_case.signin

import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmailSignIn @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(account: String,password: String): Flow<Resource<SigninResult>> = repository.emailSignIn(account,password)
}