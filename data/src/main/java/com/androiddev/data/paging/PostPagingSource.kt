package com.androiddev.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.androiddev.data.paging.cursor.PostCursor
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toPostPreview
import com.androiddev.data.util.safePagingApiCall
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.PostQuery
import com.androiddev.domain.util.Constants.PAGE_SIZE

class PostPagingSource(
    private val api: GetPostsApi,
    private val query: PostQuery
): PagingSource<PostCursor, PostPreview>() {
    override suspend fun load(params: LoadParams<PostCursor>): LoadResult<PostCursor, PostPreview> {
        return safePagingApiCall(
            apiCall =  {
                when(query) {
                    is PostQuery.New -> {
                        api.getNewPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }
                    is PostQuery.User -> {
                        api.getUserPosts(
                            userid = query.userId,
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }
                    is PostQuery.Near -> {
                        api.getNearPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            maxdistance = query.distance,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }
                    is PostQuery.NewTag -> {
                        api.getNewTagPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            tagid = query.tagId,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }
                    /**is PostQuery.New -> {
                        api.getNewPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }
                    is PostQuery.User -> {
                        api.getUserPosts(
                            userid = query.userId,
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }

                    is PostQuery.Near -> {
                        api.getNearPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            maxdistance = query.distance,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }

                    is PostQuery.NewTag -> {
                        api.getNewTagPosts(
                            postid = params.key?.postId,
                            postdate = params.key?.postDate,
                            tagid = query.tagId,
                            latitude = query.latitude,
                            longitude = query.longitude
                        )
                    }**/

                }

            },
            mapper = {
                it.posts.map { post ->
                    post.toPostPreview()

                }

            },
            nextKey = { items ->
                items.lastOrNull()?.takeIf { items.size == PAGE_SIZE }?.let {
                    PostCursor(it.postId, it.date)
                }

            }

        )
    }


    override fun getRefreshKey(state: PagingState<PostCursor, PostPreview>): PostCursor? = null

}