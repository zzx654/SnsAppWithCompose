package com.androiddev.snsappwithcompose.util

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.util.Resource


class PostPaginator (
    private val loadItems: ((Resource<GetPostsResponse>)->Unit,Boolean) -> Unit,
    private val onRefreshUpdated: (Boolean) -> Unit,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onError:  (String) -> Unit,
    private val onSuccess:  (Items:List<PostPreview>,refresh:Boolean) -> Unit
) {


    private var isMakingRequest = false

    fun loadNextItems(refresh:Boolean) {
        if(isMakingRequest) {
            return
        }
        isMakingRequest = true
        loadItems(
            { result->
                when(result) {
                    is Resource.Success -> {
                        isMakingRequest = false

                        result.data?.let {
                                onSuccess(it.posts,refresh)
                        }
                        if(refresh)
                            onRefreshUpdated(false)
                        else
                            onLoadUpdated(false)

                    }
                    is Resource.Error -> {
                        isMakingRequest = false
                        if(refresh)
                            onRefreshUpdated(false)
                        else
                            onLoadUpdated(false)
                        onError(result.message ?: "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        if(refresh)
                            onRefreshUpdated(true)
                        else
                            onLoadUpdated(true)
                    }
                }
            },
            refresh
        )

    }


}