package com.androiddev.data.repository

import android.content.res.Resources
import com.androiddev.data.R
import com.androiddev.data.remote.api.SignInApi
import com.androiddev.data.remote.dto.toSigninResponse
import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.repository.SigninRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SigninRepositoryImpl @Inject constructor(
    private val api: SignInApi,
) : SigninRepository {
    override suspend fun socialSign(
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
