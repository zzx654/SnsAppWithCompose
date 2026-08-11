package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType
import com.androiddev.domain.repository.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetPostListUseCase @Inject constructor(
    private val repository: PostListRepository,
    private val locationTracker: LocationTracker
) {
    operator fun invoke(
        type: PostListType
    ): Flow<PagingData<Post>> {
        return repository.getPosts(type)
            .onStart {
                // Flow가 수집(collect)되기 시작할 때 위치를 먼저 갱신
                locationTracker.updateLocation()
            }
    }
}