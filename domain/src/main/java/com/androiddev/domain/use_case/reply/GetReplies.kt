package com.androiddev.domain.use_case.reply

import com.androiddev.domain.model.Comments
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReplies @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(ref:Int, commentId:Int? = null, commentDate:String? = null): Flow<Resource<Comments>> = repository.getReplies(ref,commentId,commentDate)

}