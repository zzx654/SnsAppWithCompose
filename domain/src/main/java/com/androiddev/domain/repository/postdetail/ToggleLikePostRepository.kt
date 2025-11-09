package com.androiddev.domain.repository.postdetail

import com.androiddev.domain.model.ToggleLikeResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ToggleLikePostRepository {
    suspend fun toggleLikePost(postid:Int) : Flow<Resource<ToggleLikeResponse>>
}