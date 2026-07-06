package com.androiddev.domain.repository.postlist

import androidx.paging.PagingData
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.Posts
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetPostsRepository {
    suspend fun getNewTagPosts(
        postId:Int?,
        postDate:String?,
        tagId:Int,
        latitude:Double?,
        longitude:Double?,
    ):Flow<Resource<Posts>>
    suspend fun getPopularTagPosts(
        postId:Int?,
        tagId:Int,
        score:Double?,
        latitude:Double?,
        longitude:Double?,
    ):Flow<Resource<Posts>>
    suspend fun getNearPosts(
        postId:Int?,
        postDate:String?,
        maxDistance:Int,
        latitude:Double,
        longitude:Double,
    ):Flow<Resource<Posts>>
    suspend fun getNewPosts(
        postId:Int?,
        postDate:String?,
        latitude:Double?,
        longitude:Double?,
    ):Flow<Resource<Posts>>

    fun getUserPosts(
        userId:Int,
        latitude:Double?,
        longitude:Double?,
    ):Flow<PagingData<PostPreview>>

}