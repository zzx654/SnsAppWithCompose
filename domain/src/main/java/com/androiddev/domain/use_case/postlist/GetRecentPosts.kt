package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.Post
import com.androiddev.domain.repository.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecentPosts @Inject constructor(
    private val repository: PostListRepository,
    private val locationTracker: LocationTracker
) {
    operator fun invoke(tagId:Int?): Flow<PagingData<Post>> = flow {
        val locationState = locationTracker.updateLocation()
        emitAll(repository.getRecentPosts(tagId = tagId,  locationState = locationState))
    }
}