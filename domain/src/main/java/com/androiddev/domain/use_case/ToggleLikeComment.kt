package com.androiddev.domain.use_case

import com.androiddev.domain.model.ToggleLikeResponse
import com.androiddev.domain.repository.CommentRepository
import com.androiddev.domain.repository.ToggleLikePostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikeComment @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(commentId:Int): Flow<Resource<ToggleLikeResponse>> = repository.toggleLikeComment(commentId)

}