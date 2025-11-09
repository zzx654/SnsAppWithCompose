package com.androiddev.domain.use_case.tag

import com.androiddev.domain.model.GetTagsResponse
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleFavoriteTag @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tagId:Int): Flow<Resource<GetTagsResponse>> = repository.toggleFavoriteTag(tagId = tagId)
}