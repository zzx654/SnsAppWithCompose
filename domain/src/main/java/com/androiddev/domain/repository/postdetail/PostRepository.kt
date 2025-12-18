package com.androiddev.domain.repository.postdetail

import com.androiddev.domain.model.Posts
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun deletePost(postId:Int): Flow<Resource<Unit>>
    suspend fun getPost(
        postId:Int,
        latitude:Double?,
        longitude:Double?,
    ):Flow<Resource<Posts>>
}