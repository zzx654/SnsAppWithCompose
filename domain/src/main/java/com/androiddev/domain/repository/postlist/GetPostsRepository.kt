package com.androiddev.domain.repository.postlist

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

}