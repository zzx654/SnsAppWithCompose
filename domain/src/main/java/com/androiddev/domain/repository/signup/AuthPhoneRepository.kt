package com.androiddev.domain.repository.signup

import com.androiddev.domain.model.AuthCodeResult
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthPhoneRepository {
    suspend fun requestAuthCode(phoneNumber:String) : Flow<Resource<ValidationResult>>
    suspend fun authenticateCode(phoneNumber:String,authCode:String): Flow<Resource<AuthCodeResult>>
}