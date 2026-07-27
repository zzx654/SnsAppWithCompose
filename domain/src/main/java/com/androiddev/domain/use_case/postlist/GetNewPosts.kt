package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.repository.postlist.GetPostsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    operator fun invoke(
        latitude:Double? = null,
        longitude:Double? = null
    ): Flow<PagingData<PostPreview>> = repository.getNewPosts(latitude,longitude)

}