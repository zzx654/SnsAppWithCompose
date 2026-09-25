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

