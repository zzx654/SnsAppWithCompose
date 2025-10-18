package com.androiddev.domain.repository

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetPostsRepository {
    suspend fun GetPopularTagPosts(
        postId:Int?,
        tagId:Int,
        score:Double?,
        latitude:Double?,
        longitude:Double?,
    ):Flow<Resource<GetPostsResponse>>
    suspend fun GetNearPosts(
        postId:Int?,
        postDate:String?,
        maxDistance:Int,
        latitude:Double,
        longitude:Double,
    ):Flow<Resource<GetPostsResponse>>
    suspend fun GetNewPosts(
        postId:Int?,
        postDate:String?,
        latitude:Double,
        longitude:Double,
    ):Flow<Resource<GetPostsResponse>>
    suspend fun GetSelectedPost(
        postId:Int,
        latitude:Double?,
        longitude:Double?,
    ):Flow<Resource<GetPostsResponse>>
}