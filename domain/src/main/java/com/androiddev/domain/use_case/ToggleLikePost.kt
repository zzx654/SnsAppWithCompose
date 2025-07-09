package com.androiddev.domain.use_case

import com.androiddev.domain.model.ToggleLikePostResponse
import com.androiddev.domain.repository.ToggleLikePostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikePost @Inject constructor(
    private val repository: ToggleLikePostRepository
) {
    suspend operator fun invoke(postid:Int): Flow<Resource<ToggleLikePostResponse>> = repository.toggleLikePost(postid)

}