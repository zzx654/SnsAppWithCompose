package com.androiddev.domain.use_case.reply

import com.androiddev.domain.model.Comment
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostReplyUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId:Int,ref: Int,text:String,anonymousNick:String? = null): Flow<Resource<List<Comment>>> = repository.postReply(postId,ref,text,anonymousNick)
}