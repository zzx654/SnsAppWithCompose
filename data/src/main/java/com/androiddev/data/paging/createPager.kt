package com.androiddev.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

fun <Key : Any, Value : Any> createPager(
    config: PagingConfig = DefaultPagingConfig,
    pagingSourceFactory: () -> PagingSource<Key, Value>
): Flow<PagingData<Value>> {
    return Pager(
        config = config,
        pagingSourceFactory = pagingSourceFactory
    ).flow
}