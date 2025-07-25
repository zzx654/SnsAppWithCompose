package com.androiddev.snsappwithcompose.util

import com.androiddev.domain.util.Resource

class Paginator<TResponse, TItem>(
    private val loadItems: ((Resource<TResponse>) -> Unit, Boolean) -> Unit,
    private val onRefreshUpdated: (Boolean) -> Unit,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onError: (String) -> Unit,
    private val onSuccess: (items: List<TItem>, refresh: Boolean) -> Unit,
    private val extractItems: (TResponse) -> List<TItem>
) {

    private var isMakingRequest = false

    fun loadNextItems(refresh: Boolean) {
        if (isMakingRequest) return
        isMakingRequest = true

        loadItems({ result ->
            when (result) {
                is Resource.Success -> {
                    if (refresh) onRefreshUpdated(false) else onLoadUpdated(false)
                    isMakingRequest = false
                    result.data?.let { data ->
                        val items = extractItems(data)
                        onSuccess(items, refresh)
                    }

                }

                is Resource.Error -> {
                    isMakingRequest = false
                    if (refresh) onRefreshUpdated(false) else onLoadUpdated(false)
                    onError(result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    if (refresh) onRefreshUpdated(true) else onLoadUpdated(true)
                }
            }
        }, refresh)
    }
}