package com.androiddev.domain.repository.user

import com.androiddev.domain.model.Users
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getSearchedUsers(nickname:String,lastUserId:Int): Flow<Resource<Users>>
}