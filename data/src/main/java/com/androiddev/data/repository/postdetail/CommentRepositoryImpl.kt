package com.androiddev.data.repository.postdetail

import android.content.Context
import androidx.paging.PagingData
import com.androiddev.data.paging.createPager
import com.androiddev.data.paging.pagingsource.GenericPagingSource
import com.androiddev.data.paging.pagingstrategy.OldestCommentStrategy
import com.androiddev.data.paging.pagingstrategy.PopularCommentStrategy
import com.androiddev.data.remote.api.postdetail.CommentApi
import com.androiddev.data.remote.dto.toComments
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.remote.dto.toNotificationComment
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.CommentSortType
import com.androiddev.domain.model.Comments
import com.androiddev.domain.model.NotificationComment
import com.androiddev.domain.model.ToggleLikeResult
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val api: CommentApi,
    private val context: Context
): CommentRepository {

    override suspend fun getNotificationComment(commentId: Int): Flow<Resource<NotificationComment>>  =
        safeApiCall(
            context = context,
            apiCall = { api.getNotificationComment(commentId)},
            mapToResource = {
                it.toNotificationComment()
            }
        )
    override suspend fun getReplies(
        ref: Int,
        commentId: Int?,
        commentDate: String?
    ): Flow<Resource<Comments>> = safeApiCall(
        context = context,
        apiCall = { api.getReplies(ref, commentId, commentDate) },
        mapToResource = {
            it.toComments()
        }

    )

    override suspend fun getSelectedComment(
        postId: Int,
        commentId: Int
    ): Flow<Resource<Comments>> = safeApiCall(
        context = context,
        apiCall = { api.getSelectedComment(postId, commentId) },
        mapToResource = {
            it.toComments()
        }
    )

    override suspend fun getComments(
        postId: Int,
        sortType: CommentSortType
    ): Flow<PagingData<Comment>> = createPager {
        val strategy = when (sortType) {
            CommentSortType.OLDEST -> OldestCommentStrategy(api, postId)
            CommentSortType.POPULAR -> PopularCommentStrategy(api, postId)
        }
        GenericPagingSource(strategy)
    }

    override suspend fun postComment(
        postId: Int,
        text: String,
        anonymousNick: String?
    ): Flow<Resource<Comments>> = safeApiCall(
        context = context,
        apiCall = { api.postComments(postId, text, anonymousNick) },
        mapToResource = {
            it.toComments()
        }
    )

    override suspend fun postReply(
        postId: Int,
        ref: Int,
        text: String,
        anonymousNick: String?
    ): Flow<Resource<Comments>> = safeApiCall(
        context = context,
        apiCall = { api.postReply(postId, ref, text, anonymousNick) },
        mapToResource = {
            it.toComments()
        }
    )

    override suspend fun toggleLikeComment(commentId: Int): Flow<Resource<ToggleLikeResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.toggleLikeComment(commentId) },
            mapToResource = {
                it.toDomain()
            }
        )

}