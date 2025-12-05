package com.androiddev.data.repository.signup

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.signup.SignUpApi
import com.androiddev.data.remote.dto.toAuthCodeResult
import com.androiddev.data.remote.dto.toTokenResult
import com.androiddev.data.remote.dto.toValidationResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.AuthCodeResult
import com.androiddev.domain.model.TokenResult
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SignupRepositoryImpl @Inject constructor(
    private val api: SignUpApi,
    private val context: Context
) : SignupRepository {
    override suspend fun socialSignUp(
        platform: String,
        account: String,
        phonenumber: String,
        fcmToken: String
    ): Flow<Resource<TokenResult>> = safeApiCall(
        context = context,
        apiCall = { api.socialSignUp(platform,account,phonenumber,fcmToken) },
        mapToResource = { it.toTokenResult(it.token) }
    )
    override suspend fun requestAuthCode(email: String): Flow<Resource<ValidationResult>> =
        safeApiCall(
            context= context,
            apiCall = { api.requestAuthCode(email) },
            mapToResource = { it.toValidationResult(it.isValid)}
        )

    override suspend fun emailSignUp(
        account: String,
        password: String,
        phonenumber: String,
        authCode: String
    ): Flow<Resource<AuthCodeResult>> = safeApiCall(
        context = context,
        apiCall = { api.emailSignUp(account,password, phonenumber, authCode) },
        mapToResource = { it.toAuthCodeResult(isCorrect = it.isCorrect) }
    )

}

