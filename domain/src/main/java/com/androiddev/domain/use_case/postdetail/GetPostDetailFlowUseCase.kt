package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.model.Post
import com.androiddev.domain.repository.postdetail.PostRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPostDetailFlowUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): StateFlow<Post?> = repository.postDetailState
}