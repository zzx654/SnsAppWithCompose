package com.androiddev.snsappwithcompose.common.state

data class PagingState<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val isInitialized: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)