package com.androiddev.data.repository.signup

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.signup.AuthPhoneApi
import com.androiddev.data.remote.dto.toAuthCodeResult
import com.androiddev.data.remote.dto.toValidationResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.AuthCodeResult
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.repository.signup.AuthPhoneRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthPhoneRepositoryImpl @Inject constructor(
    private val api: AuthPhoneApi,
    private val context: Context
) : AuthPhoneRepository {
    override suspend fun requestAuthCode(phoneNumber: String): Flow<Resource<ValidationResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.requestAuthCode(phoneNumber) },
            mapToResource = { it.toValidationResult() }
        )

    override suspend fun authenticateCode(
        phoneNumber: String,
        authCode: String
    ): Flow<Resource<AuthCodeResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.authenticateCode(phoneNumber,authCode) },
            mapToResource = { it.toAuthCodeResult() }

        )

}