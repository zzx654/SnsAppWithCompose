package com.androiddev.domain.use_case.postlist

import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPosts @Inject constructor(
    private val repository: GetPostsRepository
){
    suspend operator fun invoke(userId:Int,postId:Int? = null,postDate:String? = null,latitude:Double? = null,longitude:Double? = null): Flow<Resource<Posts>> = repository.getUserPosts(userId,postId,postDate,latitude,longitude)
}