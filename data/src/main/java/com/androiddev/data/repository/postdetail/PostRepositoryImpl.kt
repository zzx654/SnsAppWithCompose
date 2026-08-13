package com.androiddev.data.repository.postdetail

import com.androiddev.data.remote.api.postdetail.PostApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Post
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: PostApi
): PostRepository {
    override suspend fun deletePost(postId: Int): Flow<Resource<Unit>> =
        safeApiCall(
            apiCall = { api.deletePost(postId) },
            mapToResource = {}
        )
    override suspend fun getPost(
        postId: Int,
        latitude:Double?,
        longitude:Double?
    ): Flow<Resource<List<Post>>> {
        return safeApiCall(
            apiCall = {
                api.getPost(
                    postid = postId,
                    latitude = latitude,
                    longitude = longitude
                )
            },
            mapToResource = { it.toPosts() }
        )
    }


}