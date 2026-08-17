package com.androiddev.domain.repository.postdetail


import androidx.paging.PagingData
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.CommentSortType
import com.androiddev.domain.model.Comments
import com.androiddev.domain.model.NotificationComment
import com.androiddev.domain.model.ToggleLikeResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun getNotificationComment(
        commentId:Int
    ): Flow<Resource<NotificationComment>>
    suspend fun getReplies(
        ref: Int,
        commentId: Int?,
        commentDate: String?
    ): Flow<Resource<Comments>>
    suspend fun getSelectedComment(
        postId:Int,
        commentId:Int
    ): Flow<Resource<Comments>>
    suspend fun getComments(
        postId: Int,
        sortType: CommentSortType
    ): Flow<PagingData<Comment>>

    suspend fun postComment(
        postId:Int,
        text: String,
        anonymousNick:String?
    ): Flow<Resource<Comments>>
    suspend fun postReply(
        postId: Int,
        ref: Int,
        text: String,
        anonymousNick: String?
    ): Flow<Resource<Comments>>

    suspend fun toggleLikeComment(commentId:Int) : Flow<Resource<ToggleLikeResult>>
}