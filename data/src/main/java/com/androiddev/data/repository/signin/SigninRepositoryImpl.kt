package com.androiddev.data.repository.signin

import android.content.Context
import com.androiddev.data.remote.api.signin.SignInApi
import com.androiddev.data.remote.dto.toSigninResult
import com.androiddev.data.remote.dto.toSigninWithTokenResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.model.SigninWithTokenResult
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SigninRepositoryImpl @Inject constructor(
    private val api: SignInApi,
    private val context: Context
) : SigninRepository {
    override suspend fun socialSignIn(
        platform: String,
        account: String,
        fcmToken: String
    ): Flow<Resource<SigninResult>> = safeApiCall(
        context = context,
        apiCall = { api.socialSignIn(platform,account,fcmToken) },
        mapToResource = { it.toSigninResult(
        )}
    )
    override suspend fun emailSignIn(
        account: String,
        password: String,
        fcmToken: String,
    ): Flow<Resource<SigninResult>> = safeApiCall(
        context = context,
        apiCall = { api.emailSignIn(account,password,fcmToken) },
        mapToResource = { it.toSigninResult()}
    )

    override suspend fun signInWithToken(): Flow<Resource<SigninWithTokenResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.signInWithToken() },
            mapToResource = { it.toSigninWithTokenResult()}
        )
}
