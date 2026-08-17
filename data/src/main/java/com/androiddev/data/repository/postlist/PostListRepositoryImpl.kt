package com.androiddev.data.repository.postlist

import androidx.paging.PagingData
import com.androiddev.data.paging.pagingsource.PostPagingSource
import com.androiddev.data.paging.createPager
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
    private val api:GetPostsApi,
) : PostListRepository {

    override fun getPosts(type: PostListType,location:LocationState): Flow<PagingData<Post>>
     = createPager {
        PostPagingSource(
            api = api,
            type = type,
            location = location
        )
    }

    /**{
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                // 호출하는 시점의 최신 위치를 가져와 PagingSource에 주입
                val location = locationTracker.currentLocation.value
                PostPagingSource(
                    apiService = apiService,
                    type = type,
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        ).flow
    }**/
}