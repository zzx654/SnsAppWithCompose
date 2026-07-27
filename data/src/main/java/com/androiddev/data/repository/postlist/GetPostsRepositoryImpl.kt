package com.androiddev.data.repository.postlist

import com.androiddev.data.util.safeApiCall
import android.content.Context
import androidx.paging.PagingData
import com.androiddev.data.paging.PostPagingSource
import com.androiddev.data.paging.createPager
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.PostQuery
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsRepositoryImpl @Inject constructor(
    private val api: GetPostsApi,
    private val context: Context
): GetPostsRepository {


    override suspend fun getNewTagPosts(
        postId: Int?,
        postDate:String?,
        tagId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>> = safeApiCall(
        context = context,
        apiCall = { api.getNewTagPosts(postId,postDate,tagId,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )
    override suspend fun getPopularTagPosts(
        postId: Int?,
        tagId: Int,
        score: Double?,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>>  = safeApiCall(
        context = context,
        apiCall = { api.getPopularTagPosts(postId,tagId,score,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )
    override fun getNearPosts(
        maxDistance: Int,
        latitude: Double,
        longitude: Double
    ): Flow<PagingData<PostPreview>> {
        return createPager {
            PostPagingSource(api, PostQuery.Near(maxDistance,latitude, longitude))
        }
    }




    override fun getNewPosts(
        latitude: Double?,
        longitude: Double?
    ): Flow<PagingData<PostPreview>> {
        return createPager {
            PostPagingSource(api, PostQuery.New(latitude, longitude))
        }
    }

    override fun getUserPosts(
        userId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<PagingData<PostPreview>> {
        return createPager {
            PostPagingSource(api, PostQuery.User(userId, latitude, longitude))
        }
    }




}