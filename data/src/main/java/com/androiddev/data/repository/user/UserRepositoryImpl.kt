package com.androiddev.data.repository.user

import android.content.Context
import com.androiddev.data.remote.api.user.UserApi
import com.androiddev.data.remote.dto.toUsers
import com.androiddev.data.util.safeApiCall
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
        mapToResource = { it.toUsers(it.users) }
    )
}