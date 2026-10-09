package com.androiddev.data.repository.signup

import com.androiddev.data.local.UserPreferences
import com.androiddev.data.remote.api.signup.SignUpApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.util.getFcmToken
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.AuthCodeResult
import com.androiddev.domain.model.TokenResult
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignupRepositoryImpl @Inject constructor(
    private val api: SignUpApi,
    private val userPreferences: UserPreferences,
) : SignupRepository {
    override suspend fun socialSignUp(
        platform: String,
        account: String,
        phonenumber: String
    ): Flow<Resource<TokenResult>> = safeApiCall(
        apiCall = {
            api.socialSignUp(platform,account,phonenumber, getFcmToken())
                  },
        mapToResource = {
            it.toDomain()
        },
        onSuccess = {
            if(it.token.isNotEmpty())
                userPreferences.saveAuthToken(it.token)
        }
    )
    override suspend fun requestAuthCode(email: String): Flow<Resource<ValidationResult>> =
        safeApiCall(
            apiCall = { api.requestAuthCode(email) },
            mapToResource = { it.toDomain()}
        )

    override suspend fun emailSignUp(
        account: String,
        password: String,
        phonenumber: String,
        authCode: String
    ): Flow<Resource<AuthCodeResult>> = safeApiCall(
        apiCall = { api.emailSignUp(account,password, phonenumber, authCode) },
        mapToResource = { it.toDomain() }
    )

}

