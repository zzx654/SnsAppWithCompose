package com.androiddev.data.repository.user

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.androiddev.data.paging.pagingsource.MediaPostPagingSource
import com.androiddev.data.remote.api.user.UserApi
import com.androiddev.data.remote.dto.toToggleFollowResult
import com.androiddev.data.remote.dto.toUsers
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.MediaPostQuery
import com.androiddev.domain.model.ToggleFollowResult
import com.androiddev.domain.model.Users
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api:UserApi,
    private val context:Context
): UserRepository {
    override suspend fun getSearchedUsers(
        nickname: String,
        lastUserId: Int?
    ): Flow<Resource<Users>> = safeApiCall(
        context = context,
        apiCall = { api.getSearchedUsers(nickname,lastUserId)},
        mapToResource = { it.toUsers() }
    )

    override suspend fun toggleFollowUser(userId: Int): Flow<Resource<ToggleFollowResult>> = safeApiCall(
        context = context,
        apiCall = { api.toggleFollowUser(userId)},
        mapToResource = { it.toToggleFollowResult()}
    )

    override suspend fun getUserInfo(userId: Int): Flow<Resource<Users>> = safeApiCall(
        context = context,
        apiCall = { api.getUserInfo(userId)},
        mapToResource = {
            it.toUsers(
            )
        }
    )

    override fun getMediaPosts(
        userId: Int,
        type: String,
        latitude: Double?,
        longitude: Double?
    ): Flow<PagingData<MediaPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5
            ),

            pagingSourceFactory = {
                MediaPostPagingSource(
                    api,
                    MediaPostQuery(
                        userId = userId,
                        type = type,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }
        ).flow
    }
}