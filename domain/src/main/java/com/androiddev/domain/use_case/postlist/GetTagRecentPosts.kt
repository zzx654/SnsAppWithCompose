package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.Post
import com.androiddev.domain.repository.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTagRecentPosts @Inject constructor(
    private val repository: PostListRepository,
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(tagId:Int):Flow<PagingData<Post>> {
        val locationState = locationTracker.updateLocation()
        return repository.getTagRecentPosts(tagId = tagId,locationState = locationState)
    }
}