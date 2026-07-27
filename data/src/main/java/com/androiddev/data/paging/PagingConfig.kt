package com.androiddev.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

val DefaultPagingConfig = PagingConfig(
    pageSize = 20,
    prefetchDistance = 5,
    enablePlaceholders = false // 필요 시 옵션 추가
)

fun <Key : Any, Value : Any> createPager(
    config: PagingConfig = DefaultPagingConfig,
    pagingSourceFactory: () -> PagingSource<Key, Value>
): Flow<PagingData<Value>> {
    return Pager(
        config = config,
        pagingSourceFactory = pagingSourceFactory
    ).flow
}