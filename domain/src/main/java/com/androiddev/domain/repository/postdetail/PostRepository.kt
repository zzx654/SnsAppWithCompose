package com.androiddev.domain.repository.postdetail

import com.androiddev.domain.model.Post
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PostRepository {
    val postDetailState: StateFlow<Post?>
    suspend fun deletePost(postId:Long): Flow<Resource<Unit>>
    suspend fun getPost(
        postId: Long,
        latitude: Double?,
        longitude: Double?
    ):Flow<Resource<List<Post>>>

    fun getCachedPost(postId: Long): Post?
    fun updateCachedPost(updatedPost: Post)
}