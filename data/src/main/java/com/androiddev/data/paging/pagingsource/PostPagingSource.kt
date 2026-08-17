package com.androiddev.data.paging.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.androiddev.data.paging.cursor.PostCursor
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.PagingConstants.DEFAULT_PAGE_SIZE
import com.androiddev.data.util.safePagingApiCall
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType

class PostPagingSource(
    private val api: GetPostsApi,
    private val type: PostListType,
    private val location: LocationState,
): PagingSource<PostCursor, Post>() {
    override suspend fun load(params: LoadParams<PostCursor>): LoadResult<PostCursor, Post> {

        val cursor = params.key
        val postId = cursor?.postId
        val postDate = cursor?.postDate

        return safePagingApiCall(
            apiCall =  {
                when(type) {
                    is PostListType.Recent -> {
                        api.getRecentPosts(
                            postid = postId,
                            postdate = postDate,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                    }
                    is PostListType.User -> {
                        api.getUserPosts(
                            userid = type.userId,
                            postid = postId,
                            postdate = postDate,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                    }
                    is PostListType.Nearby -> {

                        api.getNearbyPosts(
                            postid = postId,
                            postdate = postDate,
                            distancemax = type.radiusKm,
                            latitude = location.latitude?:0.0,
                            longitude = location.longitude?:0.0
                        )


                    }
                    is PostListType.TagRecent -> {
                        api.getTagRecentPosts(
                            postid = postId,
                            postdate = postDate,
                            tagid = type.tagId,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                    }
                }

            },
            mapper = { it ->
                it.toPosts()

            },
            nextKey = { items ->
                items.lastOrNull()?.takeIf { items.size == DEFAULT_PAGE_SIZE }?.let {
                    PostCursor(it.postId, it.date)
                }

            }

        )
    }


    override fun getRefreshKey(state: PagingState<PostCursor, Post>): PostCursor? = null

}