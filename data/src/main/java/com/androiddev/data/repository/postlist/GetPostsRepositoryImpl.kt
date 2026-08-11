package com.androiddev.data.repository.postlist

import com.androiddev.data.util.safeApiCall
import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSourceFactory
import com.androiddev.data.paging.PostPagingSource
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toPostPreview
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
        apiCall = { api.getTagRecentPosts(postId,postDate,tagId,latitude,longitude) },
        mapToResource = { it ->
            Posts(
            posts = it.posts.map{
                it.toPostPreview()
            } )
        }
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
    override suspend fun getNearPosts(
        postId: Int?,
        postDate: String?,
        maxDistance: Int,
        latitude: Double,
        longitude: Double
    ): Flow<Resource<Posts>> = safeApiCall(
        context = context,
        apiCall = { api.getNearbyPosts(postId,postDate,maxDistance,latitude,longitude) },
        mapToResource = { it ->
            Posts(
                posts = it.posts.map{
                    it.toPostPreview()
                } )
        }
    )

    override suspend fun getNewPosts(
        postId: Int?,
        postDate: String?,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>> =  safeApiCall(
        context = context,
        apiCall = { api.getRecentPosts(postId,postDate,latitude,longitude) },
        mapToResource = { it ->
            Posts(
                posts = it.posts.map{
                    it.toPostPreview()
                } )
        }
    )

    /**override fun getUserPosts(
        userId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<PagingData<PostPreview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                PostPagingSource(
                    api,
                    PostQuery.User(
                        userId = userId,
                        latitude = latitude,
                        longitude = longitude
                    )

                )
            }
        ).flow
    }**/




}