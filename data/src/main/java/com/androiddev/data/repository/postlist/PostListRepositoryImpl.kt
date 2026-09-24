package com.androiddev.data.repository.postlist

import androidx.paging.PagingData
import com.androiddev.data.paging.pagingsource.PostPagingSource
import com.androiddev.data.paging.createPager
import com.androiddev.data.paging.pagingsource.GenericPagingSource
import com.androiddev.data.paging.pagingstrategy.NearbyPostStrategy
import com.androiddev.data.paging.pagingstrategy.PopularPostStrategy
import com.androiddev.data.paging.pagingstrategy.RecentPostStrategy
import com.androiddev.data.paging.pagingstrategy.UserPostStrategy
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType
import com.androiddev.domain.repository.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostListRepositoryImpl @Inject constructor(
    private val api: GetPostsApi,
) : PostListRepository {


    override fun getRecentPosts(
        tagId: Int?,
        locationState: LocationState
    ): Flow<PagingData<Post>> = createPager {
        val strategy = RecentPostStrategy(api = api, tagId = tagId, locationState = locationState)
        GenericPagingSource(strategy)
    }


    override fun getPopularPosts(
        tagId: Int?,
        locationState: LocationState
    ): Flow<PagingData<Post>> = createPager {
        val strategy = PopularPostStrategy(api = api, tagId = tagId, locationState = locationState)
        GenericPagingSource(strategy)
    }

    override fun getNearbyPosts(
        radiusKm: Int,
        locationState: LocationState
    ): Flow<PagingData<Post>> = createPager {
        val strategy = NearbyPostStrategy(
            api = api,
            locationState = locationState,
            radiusKm = radiusKm
        )
        GenericPagingSource(strategy)
    }

    override fun getUserPosts(
        userId: Int,
        locationState: LocationState
    ): Flow<PagingData<Post>> = createPager {
        val strategy = UserPostStrategy(api = api, userId = userId, locationState = locationState)
        GenericPagingSource(strategy)
    }
}