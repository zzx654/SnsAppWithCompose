package com.androiddev.domain.repository.signup

import com.androiddev.domain.model.AuthCodeResult
import com.androiddev.domain.model.TokenResult
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SignupRepository {
    suspend fun socialSignUp(platform: String,account: String,phonenumber: String,fcmToken: String) : Flow<Resource<TokenResult>>
    suspend fun requestAuthCode(email:String) : Flow<Resource<ValidationResult>>

    suspend fun emailSignUp(account: String,password: String,phonenumber: String,authCode: String) : Flow<Resource<AuthCodeResult>>
}