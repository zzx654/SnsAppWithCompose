package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.GetPostsRepository
import kotlinx.coroutines.flow.Flow
import com.androiddev.domain.util.Resource
import javax.inject.Inject


class GetNearPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postId:Int? = null,postDate:String? = null,latitude:Double,longitude:Double,maxDistance:Int): Flow<Resource<GetPostsResponse>> = repository.GetNearPosts(postId,postDate,maxDistance,latitude,longitude)
}