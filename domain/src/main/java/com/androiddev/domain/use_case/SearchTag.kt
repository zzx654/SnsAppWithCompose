package com.androiddev.domain.use_case

import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.repository.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTag @Inject constructor(
    private val repository: UploadPostRepository
) {
    suspend operator fun invoke(tag:String):Flow<Resource<SearchTagResponse>> = repository.searchTag(tag)
}
