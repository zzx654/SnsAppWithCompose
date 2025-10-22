package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetTagsResponse
import com.androiddev.domain.repository.TagRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTags @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(): Flow<Resource<GetTagsResponse>> = repository.getTags()
}