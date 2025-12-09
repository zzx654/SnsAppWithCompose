package com.androiddev.domain.use_case.postlist

import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewPosts @Inject constructor(
    private val repository: GetPostsRepository
) {
    suspend operator fun invoke(postid:Int? = null,postdate:String? = null,latitude:Double? = null,longitude:Double? = null): Flow<Resource<Posts>> = repository.getNewPosts(postid,postdate,latitude,longitude)
}