package com.androiddev.domain.use_case.postlist

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewTagPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postId:Int? = null,postDate:String? = null,tagId:Int,latitude:Double,longitude:Double): Flow<Resource<GetPostsResponse>> = repository.getNewTagPosts(postId,postDate,tagId,latitude,longitude)
}