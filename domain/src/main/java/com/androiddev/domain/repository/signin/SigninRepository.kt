package com.androiddev.domain.repository.signin

import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.model.SigninWithTokenResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SigninRepository {
    suspend fun socialSignIn(platform: String,account: String,fcmToken: String) : Flow<Resource<SigninResult>>
    suspend fun emailSignIn(account: String,password: String,fcmToken: String) : Flow<Resource<SigninResult>>

    suspend fun signInWithToken() : Flow<Resource<SigninWithTokenResult>>
}