package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.repository.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class GetNearbyPosts @Inject constructor(
    private val repository: PostListRepository,
    private val locationTracker: LocationTracker
) {
    operator fun invoke(radiusKm:Int): Flow<PagingData<Post>> = flow {
        val locationState = locationTracker.updateLocation()

        emitAll(repository.getNearbyPosts(radiusKm = radiusKm, locationState = locationState))
    }
}