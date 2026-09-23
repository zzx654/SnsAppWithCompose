package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.PostCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.PostsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import retrofit2.Response

class UserPostStrategy(
    private val api:GetPostsApi,
    private val userId:Int? = null,
    private val locationState:LocationState?
): PagingStrategy<PostsDto, Post,PostCursor.Recent> {
    override suspend fun fetch(cursor: PostCursor.Recent?): Response<BaseApiResponse<PostsDto>> {
        return api.getUserPosts(
            userid = userId,
            postid = cursor?.postId,
            postdate = cursor?.postDate,
            latitude = locationState?.latitude,
            longitude = locationState?.longitude
        )
    }

    override fun mapToDomain(data: PostsDto): List<Post> {
        return data.posts.map { it.toDomain() }
    }

    override fun extractNextCursor(items: List<Post>, pageSize: Int): PostCursor.Recent? {
        if (items.size < pageSize) return null
        val lastItem = items.lastOrNull() ?: return null
        return PostCursor.Recent(postId = lastItem.postId, postDate = lastItem.date)
    }
}