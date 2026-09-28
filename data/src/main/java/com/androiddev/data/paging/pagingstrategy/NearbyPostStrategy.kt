package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.PostCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.PostsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import retrofit2.Response

class NearbyPostStrategy(
    private val api:GetPostsApi,
    private val radiusKm:Int,
    private val locationState:LocationState
): PagingStrategy<PostsDto, Post,PostCursor.Recent> {
    override suspend fun fetch(cursor: PostCursor.Recent?): Response<BaseApiResponse<PostsDto>> {
        return api.getNearbyPosts(
            postid = cursor?.postId,
            postdate = cursor?.postDate,
            distancemax = radiusKm,
            latitude = locationState.latitude?:0.0,
            longitude = locationState.longitude?:0.0
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