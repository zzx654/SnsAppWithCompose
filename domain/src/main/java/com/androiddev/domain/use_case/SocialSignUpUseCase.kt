package com.androiddev.domain.use_case

import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SocialSignUpUseCase @Inject constructor(
private val repository: SignupRepository
) {
    suspend operator fun invoke(platform: String,account: String,phonenumber: String): Flow<Resource<String>> = repository.socialSignUp(platform,account,phonenumber)
}