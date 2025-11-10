package com.androiddev.domain.use_case.postlist

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularTagPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postId:Int? = null,tagId:Int,score:Double?,latitude:Double,longitude:Double): Flow<Resource<Posts>> = repository.getPopularTagPosts(postId,tagId,score,latitude,longitude)
}