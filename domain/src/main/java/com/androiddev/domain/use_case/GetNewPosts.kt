package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postid:Int? = null,postdate:String? = null,latitude:Double,longitude:Double): Flow<Resource<GetPostsResponse>> = repository.getNewPosts(postid,postdate,latitude,longitude)
}