package com.androiddev.domain.repository.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType
import kotlinx.coroutines.flow.Flow

interface PostListRepository {
    fun getPosts(type: PostListType,location:LocationState): Flow<PagingData<Post>>
}