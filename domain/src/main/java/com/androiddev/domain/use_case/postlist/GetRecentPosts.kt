package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.repository.postlist.PostListRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentPosts @Inject constructor(
    private val repository: PostListRepository,
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke():Flow<PagingData<Post>> {
        val locationState = locationTracker.updateLocation()
        return repository.getRecentPosts(locationState = locationState)

    }
}