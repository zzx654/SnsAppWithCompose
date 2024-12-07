package com.androiddev.data.repository

import android.content.res.Resources
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
) : SignupRepository {
    override suspend fun socialSignUp(
        platform: String,
        account: String
    ): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.socialSignUp(platform,account).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.token))
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
    override suspend fun requestAuthCode(email: String): Flow<Resource<Unit>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.requestAuthCode(email).body()?.let{ result ->
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

}

