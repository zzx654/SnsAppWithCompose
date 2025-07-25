package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.repository.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetComments @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postid:Int, commentid:Int? = null, commentdate:String? = null): Flow<Resource<GetCommentsResponse>> = repository.getComments(postid,commentid,commentdate)

}