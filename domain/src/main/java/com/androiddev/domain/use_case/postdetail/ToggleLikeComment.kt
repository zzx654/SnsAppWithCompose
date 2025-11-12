package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.model.ToggleLikeResult
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikeComment @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(commentId:Int): Flow<Resource<ToggleLikeResult>> = repository.toggleLikeComment(commentId)

}