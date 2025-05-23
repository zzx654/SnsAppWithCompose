package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedPost @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postid:Int,latitude:Double? = null,longitude:Double? = null): Flow<Resource<GetPostsResponse>> = repository.GetSelectedPost(postid,latitude,longitude)
}