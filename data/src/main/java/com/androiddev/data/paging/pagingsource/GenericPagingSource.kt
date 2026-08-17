package com.androiddev.data.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.androiddev.data.paging.pagingstrategy.PagingStrategy
import com.androiddev.data.util.PagingConstants.DEFAULT_PAGE_SIZE
import com.androiddev.data.util.safePagingApiCall

class GenericPagingSource<T, R : Any, C : Any>(
    private val strategy: PagingStrategy<T, R, C>
) : PagingSource<C, R>() {
    //T는 DTO 타입, C는 커서 R은 도메인 모델

    override suspend fun load(params: LoadParams<C>): LoadResult<C, R> {
        return safePagingApiCall(
            apiCall = {
                strategy.fetch(
                    cursor = params.key
                )
            },
            mapper = { data ->
                strategy.mapToDomain(data)
            },
            nextKey = { items ->
                strategy.extractNextCursor(items, DEFAULT_PAGE_SIZE)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<C, R>): C? = null
}