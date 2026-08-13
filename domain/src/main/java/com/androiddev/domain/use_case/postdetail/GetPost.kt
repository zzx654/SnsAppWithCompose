package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetPost @Inject constructor(
    private val repository: PostRepository,
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(postId: Int): Flow<Resource<List<Post>>> {

        locationTracker.updateLocation()

        val location = locationTracker.currentLocation.value

        return repository.getPost(
            postId = postId,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}