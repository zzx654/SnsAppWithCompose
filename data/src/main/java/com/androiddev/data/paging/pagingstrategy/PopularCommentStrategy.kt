package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.CommentCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.postdetail.CommentApi
import com.androiddev.data.remote.dto.CommentsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.model.Comment
import retrofit2.Response

class PopularCommentStrategy(
    private val api: CommentApi,
    private val postId: Int
) : PagingStrategy<CommentsDto, Comment, CommentCursor.Popular> {

    override suspend fun fetch(
        cursor: CommentCursor.Popular?,
    ): Response<BaseApiResponse<CommentsDto>> {
        return api.getPopularComments(
            postid = postId,
            commentid = cursor?.commentId,
            score = cursor?.commentScore
        )
    }

    override fun mapToDomain(data: CommentsDto): List<Comment> =
        data.comments.map{ it.toDomain()}

    override fun extractNextCursor(items: List<Comment>, pageSize:Int): CommentCursor.Popular? {
        if (items.size < pageSize) return null
        val lastItem = items.lastOrNull() ?: return null
        return lastItem.commentId?.let {
            CommentCursor.Popular(
                commentId = it,
                commentScore = lastItem.score
            )
        }
    }
}