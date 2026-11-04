package com.androiddev.domain.repository.postdetail


import androidx.paging.PagingData
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.CommentSortType
import com.androiddev.domain.model.NotificationComment
import com.androiddev.domain.model.ToggleLikeResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun getNotificationComment(
        commentId: Long
    ): Flow<Resource<NotificationComment>>
    suspend fun getReplies(
        ref: Long
    ): Flow<PagingData<Comment>>
    suspend fun getSelectedComment(
        commentId: Long
    ): Flow<Resource<List<Comment>>>
    suspend fun getComments(
        postId: Long,
        sortType: CommentSortType
    ): Flow<PagingData<Comment>>

    suspend fun postComment(
        postId: Long,
        text: String,
        anonymousNick:String?
    ): Flow<Resource<List<Comment>>>
    suspend fun postReply(
        postId: Long,
        ref: Long,
        text: String,
        anonymousNick: String?
    ): Flow<Resource<List<Comment>>>

    suspend fun toggleLikeComment(commentId:Long) : Flow<Resource<ToggleLikeResult>>
}