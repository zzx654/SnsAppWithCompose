package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.repository.postlist.GetPostsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetNearPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    operator fun invoke(
        maxDistance: Int,
        latitude:Double,
        longitude:Double
    ): Flow<PagingData<PostPreview>> = repository.getNearPosts(maxDistance,latitude,longitude)

}