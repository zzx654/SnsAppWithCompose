package com.androiddev.data.repository.postlist

import com.androiddev.data.util.safeApiCall
import android.content.Context
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsRepositoryImpl @Inject constructor(
    private val api: GetPostsApi,
    private val context: Context
): GetPostsRepository {
    override suspend fun getNewTagPosts(
        postId: Int?,
        postDate:String?,
        tagId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>> = safeApiCall(
        context = context,
        apiCall = { api.getNewTagPosts(postId,postDate,tagId,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )
    override suspend fun getPopularTagPosts(
        postId: Int?,
        tagId: Int,
        score: Double?,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>>  = safeApiCall(
        context = context,
        apiCall = { api.getPopularTagPosts(postId,tagId,score,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )
    override suspend fun getNearPosts(
        postId: Int?,
        postDate: String?,
        maxDistance: Int,
        latitude: Double,
        longitude: Double
    ): Flow<Resource<Posts>> = safeApiCall(
        context = context,
        apiCall = { api.getNearPosts(postId,postDate,maxDistance,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )

    override suspend fun getNewPosts(
        postId: Int?,
        postDate: String?,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>> =  safeApiCall(
        context = context,
        apiCall = { api.getNewPosts(postId,postDate,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )

    override suspend fun getUserPosts(
        userId: Int?,
        postId: Int?,
        postDate: String?,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>> = safeApiCall(
        context = context,
        apiCall = { api.getUserPosts(userId,postId,postDate,latitude,longitude)},
        mapToResource = { it.toPosts()}
    )




}