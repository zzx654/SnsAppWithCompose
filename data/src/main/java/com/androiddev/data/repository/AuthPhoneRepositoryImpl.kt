package com.androiddev.data.repository

import android.content.res.Resources
import com.androiddev.data.R
import com.androiddev.data.remote.api.AuthPhoneApi
import com.androiddev.domain.repository.AuthPhoneRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthPhoneRepositoryImpl @Inject constructor(
    private val api: AuthPhoneApi
) : AuthPhoneRepository {
    override suspend fun requestAuthCode(phoneNumber: String): Flow<Resource<Unit>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.requestAuthCode(phoneNumber).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(Unit))
                    }
                    else
                        emit(Resource.Error(Resources.getSystem().getString(R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: Resources.getSystem().getString(R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(Resources.getSystem().getString(R.string.connection_error)))
            }
        }
    }

    override suspend fun authenticateCode(
        phoneNumber: String,
        authCode: String
    ): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.authenticateCode(phoneNumber,authCode).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.isCorrect))
                    }
                    else
                        emit(Resource.Error(Resources.getSystem().getString(R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: Resources.getSystem().getString(R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(Resources.getSystem().getString(R.string.connection_error)))
            }
        }
    }

}