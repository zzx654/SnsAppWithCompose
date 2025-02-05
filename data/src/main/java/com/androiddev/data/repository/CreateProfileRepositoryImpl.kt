package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.CreateProfileApi
import com.androiddev.domain.repository.CreateProfileRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CreateProfileRepositoryImpl @Inject constructor(
    private val api: CreateProfileApi,
    private val context: Context
) : CreateProfileRepository {
    override suspend fun uploadImage(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
        birth: Int,
        gender: RequestBody
    ): Flow<Resource<Boolean>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.uploadimg(profileImage,nickname,birth,gender).body()?.let { result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.isTokenValid))
                    } else {
                        emit(Resource.Error(getString(context,R.string.server_error)))
                    }
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }

        }
    }

    override suspend fun checkNickname(nickname: String): Flow<Resource<Boolean>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.checkNickname(nickname).body()?.let { result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.isValid))
                    } else {
                        emit(Resource.Error(getString(context,R.string.server_error)))
                    }
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }

        }
    }

}