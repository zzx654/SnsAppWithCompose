package com.androiddev.data.util

import androidx.paging.PagingSource
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.domain.exception.ConnectionException
import com.androiddev.domain.exception.ServerException
import com.androiddev.domain.exception.TokenExpiredException
import com.androiddev.domain.exception.UnknownException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T, R : Any, K: Any> safePagingApiCall(
    apiCall: suspend () -> Response<BaseApiResponse<T>>,
    mapper: (T) -> List<R>,
    nextKey: (List<R>) -> K?
): PagingSource.LoadResult<K, R> {

    return try {

        val body = apiCall().body()
            ?: return PagingSource.LoadResult.Error(ServerException())

        if (body.isTokenValid == false) {
            return PagingSource.LoadResult.Error(TokenExpiredException())
        }

        if (body.resultCode != 200) {
            return PagingSource.LoadResult.Error(ServerException())
        }

        val list = body.data?.let(mapper).orEmpty()

        PagingSource.LoadResult.Page(
            data = list,
            prevKey = null,
            nextKey = nextKey(list)
        )

    } catch (e: IOException) {

        PagingSource.LoadResult.Error(ConnectionException())

    } catch (e: HttpException) {

        PagingSource.LoadResult.Error(ServerException())

    } catch (e: Exception) {

        PagingSource.LoadResult.Error(UnknownException())

    }

}


