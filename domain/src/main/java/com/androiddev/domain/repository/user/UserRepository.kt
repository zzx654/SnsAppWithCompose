package com.androiddev.domain.repository.user

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.ToggleFollowResult
import com.androiddev.domain.model.User
import com.androiddev.domain.model.Users
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getSearchedUsers(nickname:String): Flow<PagingData<User>>
    suspend fun toggleFollowUser(userId:Int): Flow<Resource<ToggleFollowResult>>
    suspend fun getUserInfo(userId:Int): Flow<Resource<List<User>>>
    fun getMediaPosts(userId:Int,type:String,locationState: LocationState):Flow<PagingData<MediaPost>>
}