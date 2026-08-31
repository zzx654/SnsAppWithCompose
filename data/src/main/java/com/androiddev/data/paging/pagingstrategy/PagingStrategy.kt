package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.remote.BaseApiResponse
import retrofit2.Response

interface PagingStrategy<T, R : Any, C : Any> {


    suspend fun fetch(cursor: C?): Response<BaseApiResponse<T>>

    fun mapToDomain(data: T): List<R>

    fun extractNextCursor(items: List<R>, pageSize: Int): C?
}