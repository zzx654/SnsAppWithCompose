package com.androiddev.domain.use_case.signin

import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SocialSignIn @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(platform: String,account: String): Flow<Resource<SigninResult>> = repository.socialSignIn(platform,account)
}