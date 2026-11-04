package com.androiddev.domain.repository.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostListRepository {
    fun getRecentPosts(tagId: Long? = null,locationState: LocationState): Flow<PagingData<Post>>
    //fun getPopularPosts(): Flow<PagingData<Post>>
    fun getPopularPosts(tagId: Long? = null, locationState: LocationState): Flow<PagingData<Post>>
    fun getNearbyPosts(radiusKm:Int,locationState: LocationState): Flow<PagingData<Post>>
    fun getUserPosts(userId: Long, locationState: LocationState): Flow<PagingData<Post>>
    //fun getPosts(type: PostListType,location:LocationState): Flow<PagingData<Post>>
}