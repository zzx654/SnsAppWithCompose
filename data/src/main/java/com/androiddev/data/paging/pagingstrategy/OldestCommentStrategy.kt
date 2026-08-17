package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.CommentCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.postdetail.CommentApi
import com.androiddev.data.remote.dto.CommentsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.model.Comment
import retrofit2.Response

class OldestCommentStrategy(
    private val api: CommentApi,
    private val postId: Int
) : PagingStrategy<CommentsDto, Comment, CommentCursor.Default> {

    override suspend fun fetch(
        cursor: CommentCursor.Default?,
    ): Response<BaseApiResponse<CommentsDto>> {
        return api.getComments(
            postid = postId,
            commentid = cursor?.commentId,
            commentdate = cursor?.commentDate
        )
    }

    override fun mapToDomain(data: CommentsDto): List<Comment> =
        data.comments.map{ it.toDomain()}

    override fun extractNextCursor(items: List<Comment>,pageSize:Int): CommentCursor.Default? {
        if (items.size < pageSize) return null
        val lastItem = items.lastOrNull() ?: return null
        return lastItem.commentId?.let {
            CommentCursor.Default(
                commentId = it,
                commentDate = lastItem.date
            )
        }
    }
}