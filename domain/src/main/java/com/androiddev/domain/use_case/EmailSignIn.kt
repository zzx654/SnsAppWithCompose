package com.androiddev.domain.use_case

import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmailSignIn @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(account: String,password: String): Flow<Resource<SigninResponse>> = repository.emailSignIn(account,password)
}