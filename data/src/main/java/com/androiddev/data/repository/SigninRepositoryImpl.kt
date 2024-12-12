package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.SignInApi
import com.androiddev.data.remote.dto.toSigninResponse
import com.androiddev.data.remote.dto.toSigninWithTokenResponse
import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.model.SigninWithTokenResponse
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SigninRepositoryImpl @Inject constructor(
    private val api: SignInApi,
    private val context: Context
) : SigninRepository {
    override suspend fun socialSignIn(
        platform: String,
        account: String
    ): Flow<Resource<SigninResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.socialSignIn(platform,account).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        val signinResponse = result.toSigninResponse(result.isMember,result.profileWritten,result.token)
                        emit(Resource.Success(signinResponse))
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
    override suspend fun emailSignIn(
        account: String,
        password: String
    ): Flow<Resource<SigninResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.emailSignIn(account,password).body()?.let{ result ->
                    if(result.resultCode == 200) {

                        val signinResponse = result.toSigninResponse(result.isMember,result.profileWritten,result.token)
                        emit(Resource.Success(signinResponse))
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

    override suspend fun signInWithToken(): Flow<Resource<SigninWithTokenResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.signInWithToken().body()?.let{ result ->
                    if(result.resultCode == 200) {
                        val signinResponse = result.toSigninWithTokenResponse(result.signInResult,result.profileWritten)
                        emit(Resource.Success(signinResponse))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            }catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }
        }
    }
}
