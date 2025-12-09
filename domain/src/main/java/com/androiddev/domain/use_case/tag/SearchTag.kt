package com.androiddev.domain.use_case.tag

import com.androiddev.domain.model.SearchedTags
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTag @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tag:String):Flow<Resource<SearchedTags>> = repository.searchTag(tag)
}
