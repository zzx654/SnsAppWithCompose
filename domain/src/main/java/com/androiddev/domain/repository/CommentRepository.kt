package com.androiddev.domain.repository


import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun getComments(
        postId: Int,
        commentId: Int?,
        commentDate: String?
    ): Flow<Resource<GetCommentsResponse>>

    suspend fun postComment(
        postId:Int,
        text: String,
        anonymousNick:String?
    ): Flow<Resource<GetCommentsResponse>>
}