package com.androiddev.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.androiddev.data.paging.cursor.PostCursor
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toPost
import com.androiddev.data.remote.dto.toPostPreview
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.safePagingApiCall
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.PostQuery
import com.androiddev.domain.util.Constants.PAGE_SIZE

class PostPagingSource(
    private val api: GetPostsApi,
    private val type: PostListType,
    private val location: LocationState,
): PagingSource<PostCursor, Post>() {
    override suspend fun load(params: LoadParams<PostCursor>): LoadResult<PostCursor, Post> {
        return safePagingApiCall(
            apiCall =  {
                when(type) {
                    is PostListType.Recent -> {
                        api.getRecentPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                    }
                    is PostListType.User -> {
                        api.getUserPosts(
                            userid = type.userId,
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                    }
                    is PostListType.Nearby -> {

                        api.getNearbyPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            distancemax = type.radiusKm,
                            latitude = location.latitude?:0.0,
                            longitude = location.longitude?:0.0
                        )


                    }
                    is PostListType.TagRecent -> {
                        api.getTagRecentPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
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
                items.lastOrNull()?.takeIf { items.size == PAGE_SIZE }?.let {
                    PostCursor(it.postId, it.date)
                }

            }

        )
    }


    override fun getRefreshKey(state: PagingState<PostCursor, Post>): PostCursor? = null

}