package com.androiddev.domain.use_case.tag


import com.androiddev.domain.model.Tags
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleFavoriteTag @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke(tagId: Long): Flow<Resource<Tags>> = repository.toggleFavoriteTag(tagId = tagId)
}