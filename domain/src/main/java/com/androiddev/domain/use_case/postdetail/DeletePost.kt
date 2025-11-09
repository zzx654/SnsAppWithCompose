package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletePost @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId:Int): Flow<Resource<Boolean>> = repository.deletePost(postId)

}