package com.androiddev.domain.repository

import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.model.SigninWithTokenResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SigninRepository {
    suspend fun socialSignIn(platform: String,account: String) : Flow<Resource<SigninResponse>>
    suspend fun emailSignIn(account: String,password: String) : Flow<Resource<SigninResponse>>

    suspend fun signInWithToken() : Flow<Resource<SigninWithTokenResponse>>
}