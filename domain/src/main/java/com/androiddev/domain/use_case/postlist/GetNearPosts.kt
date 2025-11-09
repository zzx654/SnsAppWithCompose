package com.androiddev.domain.use_case.postlist

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.postlist.GetPostsRepository
import kotlinx.coroutines.flow.Flow
import com.androiddev.domain.util.Resource
import javax.inject.Inject


class GetNearPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postid:Int? = null,postdate:String? = null,latitude:Double,longitude:Double,maxDistance:Int): Flow<Resource<GetPostsResponse>> = repository.getNearPosts(postid,postdate,maxDistance,latitude,longitude)
}