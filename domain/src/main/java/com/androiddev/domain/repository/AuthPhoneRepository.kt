package com.androiddev.domain.repository

import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthPhoneRepository {
    suspend fun requestAuthCode(phoneNumber:String) : Flow<Resource<Unit>>
    suspend fun authenticateCode(phoneNumber:String,authCode:String): Flow<Resource<Boolean>>
}