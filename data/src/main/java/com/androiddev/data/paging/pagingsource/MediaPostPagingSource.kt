package com.androiddev.data.paging.pagingsource



import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.androiddev.data.remote.api.user.UserApi
import com.androiddev.data.remote.dto.toMediaPost
import com.androiddev.data.util.safePagingApiCall
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.MediaPostQuery
import com.androiddev.domain.util.Constants.PAGE_SIZE

class MediaPostPagingSource(
    private val api: UserApi,
    private val query: MediaPostQuery
): PagingSource<Int,MediaPost>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaPost> {
        return safePagingApiCall(
            apiCall = {

                api.getMedia(
                    userid = query.userId,
                    type = query.type,
                    mediaid = params.key,

                    latitude = query.latitude,
                    longitude = query.longitude


                )
            },
            mapper = {
                it.mediaPosts.map { mediaPost ->
                    mediaPost.toMediaPost()

                }
            },
            nextKey = { items ->

                items.lastOrNull()?.id?.takeIf {
                    items.size == PAGE_SIZE
                }
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, MediaPost>): Int? = null

}