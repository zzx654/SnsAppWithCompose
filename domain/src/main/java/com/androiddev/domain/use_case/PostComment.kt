package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.repository.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostComment @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId:Int,text:String,anonymousNick:String? = null): Flow<Resource<GetCommentsResponse>> = repository.postComment(postId,text,anonymousNick)
}