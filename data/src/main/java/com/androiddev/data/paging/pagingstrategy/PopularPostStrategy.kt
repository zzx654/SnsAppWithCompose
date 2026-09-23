package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.PostCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.PostsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import retrofit2.Response

class PopularPostStrategy(
    private val api:GetPostsApi,
    private val tagId:Int? = null,
    private val locationState:LocationState?
): PagingStrategy<PostsDto, Post,PostCursor.Popular> {
    override suspend fun fetch(cursor: PostCursor.Popular?): Response<BaseApiResponse<PostsDto>> {
        return api.getTagPopularPosts(
            tagid = tagId!!,
            postid = cursor?.postId,
            score = cursor?.postScore,
            latitude = locationState?.latitude,
            longitude = locationState?.longitude
        )
    }

    override fun mapToDomain(data: PostsDto): List<Post> {
        return data.posts.map { it.toDomain() }
    }

    override fun extractNextCursor(items: List<Post>, pageSize: Int): PostCursor.Popular? {
        if (items.size < pageSize) return null
        val lastItem = items.lastOrNull() ?: return null
        return PostCursor.Popular(postId = lastItem.postId, postScore = lastItem.popularityScore)
    }
}