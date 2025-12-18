package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPost @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postid:Int,latitude:Double? = null,longitude:Double? = null): Flow<Resource<Posts>> = repository.getPost(postid,latitude,longitude)
}