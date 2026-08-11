package com.androiddev.data.util

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.HttpException
import java.io.IOException
import com.androiddev.data.R
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.domain.util.DataError

fun <T, R> safeApiCall(
    context: Context,
    apiCall: suspend () -> Response<BaseApiResponse<T>>,
    mapToResource: (T) -> R
): Flow<Resource<R>> = flow {
    try {
        emit(Resource.Loading())
        val response = apiCall()

        response.body()?.let { result ->
            val tokenValid = result.isTokenValid ?: true

            if (!tokenValid) {
                // 토큰 처리
                emit(Resource.TokenExpired<R>()) // 타입 안전!
            } else if (result.resultCode == 200 ) {
                    emit(Resource.Success(result.data?.let(mapToResource)))
            } else {
                emit(Resource.Error(getString(context, com.androiddev.data.R.string.server_error)))
            }
        }
    } catch (e: HttpException) {
        emit(Resource.Error(e.localizedMessage ?: getString(context, com.androiddev.data.R.string.unexpected_error)))
    } catch (e: IOException) {
        emit(Resource.Error(getString(context, com.androiddev.data.R.string.connection_error)))
    }
}
fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<BaseApiResponse<T>>,
    mapToResource: (T) -> R
): Flow<Resource<R>> = flow {
    try {
        emit(Resource.Loading())
        val response = apiCall()

        response.body()?.let { result ->
            val tokenValid = result.isTokenValid ?: true

            if (!tokenValid) {
                emit(Resource.TokenExpired())
            } else if (result.resultCode == 200) {
                emit(Resource.Success(result.data?.let(mapToResource)))
            } else {
                // R.string 대신 Enum을 던짐!
                emit(Resource.Error(DataError.Network.SERVER_ERROR))
            }
        }
    } catch (e: IOException) {
        emit(Resource.Error(DataError.Network.CONNECTION_ERROR))
    } catch (e: Exception) {
        emit(Resource.Error(DataError.Network.UNEXPECTED_ERROR))
    }
}