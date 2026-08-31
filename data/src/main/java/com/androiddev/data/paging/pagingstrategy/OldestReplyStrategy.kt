package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.CommentCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.postdetail.CommentApi
import com.androiddev.data.remote.dto.CommentsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.model.Comment
import retrofit2.Response

class OldestReplyStrategy(
    private val api: CommentApi,
    private val ref: Int
) : PagingStrategy<CommentsDto, Comment, CommentCursor.Default> {

    override suspend fun fetch(
        cursor: CommentCursor.Default?,
    ): Response<BaseApiResponse<CommentsDto>> {
        return api.getReplies(
            ref = ref,
            commentid = cursor?.commentId,
            commentdate = cursor?.commentDate
        )
    }

    override fun mapToDomain(data: CommentsDto): List<Comment> =
        data.comments.map{ it.toDomain()}

    override fun extractNextCursor(items: List<Comment>, pageSize:Int): CommentCursor.Default? {
        if (items.size < pageSize) return null
        val lastItem = items.lastOrNull() ?: return null
        return CommentCursor.Default(
            commentId = lastItem.commentId,
            commentDate = lastItem.date
        )

    }
}