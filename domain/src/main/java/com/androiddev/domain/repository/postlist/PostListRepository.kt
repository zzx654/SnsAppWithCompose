package com.androiddev.domain.repository.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType
import kotlinx.coroutines.flow.Flow

interface PostListRepository {
    fun getTagRecentPosts(tagId:Int,locationState: LocationState): Flow<PagingData<Post>>
    fun getRecentPosts(locationState: LocationState): Flow<PagingData<Post>>
    //fun getPopularPosts(): Flow<PagingData<Post>>
    fun getTagPopularPosts(tagId:Int, locationState: LocationState): Flow<PagingData<Post>>
    fun getNearbyPosts(radiusKm:Int,locationState: LocationState): Flow<PagingData<Post>>
    fun getUserPosts(userId: Int,locationState: LocationState): Flow<PagingData<Post>>
    //fun getPosts(type: PostListType,location:LocationState): Flow<PagingData<Post>>
}