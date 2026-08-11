package com.androiddev.data.paging

import androidx.paging.PagingConfig

val DefaultPagingConfig = PagingConfig(
    pageSize = 20,
    prefetchDistance = 5,
    enablePlaceholders = false // 필요 시 옵션 추가
)