package com.androiddev.data.repository

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.SignInApi
import com.androiddev.data.remote.api.SignUpApi
import com.androiddev.data.remote.dto.toSigninResponse
import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.repository.SignupRepository
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
        phonenumber: String
    ): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.socialSignUp(platform,account,phonenumber).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.token))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }

        }
    }
    override suspend fun requestAuthCode(email: String): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.requestAuthCode(email).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.exist))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }
        }
    }

    override suspend fun emailSignUp(
        account: String,
        password: String,
        phonenumber: String,
        authCode: String
    ): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.emailSignUp(account,password, phonenumber, authCode).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.isCorrect))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }

        }
    }

}

