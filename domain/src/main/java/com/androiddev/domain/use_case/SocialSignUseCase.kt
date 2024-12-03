package com.androiddev.domain.use_case

import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SocialSignUseCase @Inject constructor(
    private val repository: SigninRepository
) {
    suspend operator fun invoke(platform: String,account: String): Flow<Resource<SigninResponse>> = repository.socialSign(platform,account)
}