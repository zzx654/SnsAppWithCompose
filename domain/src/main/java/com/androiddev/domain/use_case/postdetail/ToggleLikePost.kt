package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.model.ToggleLikeResponse
import com.androiddev.domain.repository.postdetail.ToggleLikePostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikePost @Inject constructor(
    private val repository: ToggleLikePostRepository
) {
    suspend operator fun invoke(postId:Int): Flow<Resource<ToggleLikeResponse>> = repository.toggleLikePost(postId)

}