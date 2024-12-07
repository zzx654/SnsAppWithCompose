package com.androiddev.domain.repository

import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.model.SocialSignupResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SignupRepository {
    suspend fun socialSignUp(platform: String,account: String) : Flow<Resource<String>>
    suspend fun requestAuthCode(email:String) : Flow<Resource<Unit>>
}