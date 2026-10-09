package com.androiddev.data.repository.user

import android.content.Context
import androidx.paging.PagingData
import com.androiddev.data.paging.createPager
import com.androiddev.data.paging.pagingsource.GenericPagingSource
import com.androiddev.data.paging.pagingstrategy.MediaPostStrategy
import com.androiddev.data.paging.pagingstrategy.UserStrategy
import com.androiddev.data.remote.api.user.UserApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.ToggleFollowResult
import com.androiddev.domain.model.User
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api:UserApi,
    private val context:Context
): UserRepository {
    override fun getSearchedUsers(
        nickname: String
    ): Flow<PagingData<User>> = createPager {
        val strategy = UserStrategy(api = api, nickname = nickname)
        GenericPagingSource(strategy)
    }

    override suspend fun toggleFollowUser(userId: Int): Flow<Resource<ToggleFollowResult>> = safeApiCall(
        context = context,
        apiCall = { api.toggleFollowUser(userId)},
        mapToResource = { it.toDomain()}
    )

    override suspend fun getUserInfo(userId: Int): Flow<Resource<List<User>>> = safeApiCall(
        context = context,
        apiCall = { api.getUserInfo(userId) },
        mapToResource = {
            it.users.map{ user -> user.toDomain()}
        }
    )

    override fun getMediaPosts(
        userId: Int,
        type: String,
        locationState:LocationState
    ): Flow<PagingData<MediaPost>> = createPager{
        val strategy = MediaPostStrategy(api = api,userId = userId,type = type,locationState = locationState)
        GenericPagingSource(strategy)
    }
}