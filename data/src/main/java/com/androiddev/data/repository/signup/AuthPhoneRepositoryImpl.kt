package com.androiddev.data.repository.signup

import com.androiddev.data.remote.api.signup.AuthPhoneApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.AuthCodeResult
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.repository.signup.AuthPhoneRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthPhoneRepositoryImpl @Inject constructor(
    private val api: AuthPhoneApi
) : AuthPhoneRepository {
    override suspend fun requestAuthCode(phoneNumber: String): Flow<Resource<ValidationResult>> =
        safeApiCall(
            apiCall = { api.requestAuthCode(phoneNumber) },
            mapToResource = { it.toDomain() }
        )

    override suspend fun authenticateCode(
        phoneNumber: String,
        authCode: String
    ): Flow<Resource<AuthCodeResult>> =
        safeApiCall(
            apiCall = { api.authenticateCode(phoneNumber,authCode) },
            mapToResource = { it.toDomain() }

        )

}