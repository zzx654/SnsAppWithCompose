package com.androiddev.domain.repository.postdetail

import com.androiddev.domain.model.Post
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PostRepository {
    val postDetailState: StateFlow<Post?>
    suspend fun deletePost(postId:Int): Flow<Resource<Unit>>
    suspend fun getPost(
        postId:Int,
        latitude:Double?,
        longitude:Double?
    ):Flow<Resource<List<Post>>>

    fun getCachedPost(postId: Int): Post?
    fun updateCachedPost(updatedPost: Post)
}