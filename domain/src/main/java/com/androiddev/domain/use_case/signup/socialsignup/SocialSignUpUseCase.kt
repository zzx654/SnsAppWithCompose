package com.androiddev.domain.use_case.signup.socialsignup

import com.androiddev.domain.model.TokenResult
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SocialSignUpUseCase @Inject constructor(
private val repository: SignupRepository
) {
    suspend operator fun invoke(platform: String,account: String,phoneNumber: String,fcmToken: String): Flow<Resource<TokenResult>> = repository.socialSignUp(platform,account,phoneNumber,fcmToken)
}