package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.model.Comments
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularComments @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId:Int, commentId:Int? = null, score:Int? = null): Flow<Resource<Comments>> = repository.getPopularComments(postId,commentId,score?:0)

}