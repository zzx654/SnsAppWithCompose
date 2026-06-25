package com.androiddev.domain.repository.user

import androidx.paging.PagingData
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.MediaPosts
import com.androiddev.domain.model.ToggleFollowResult
import com.androiddev.domain.model.User
import com.androiddev.domain.model.Users
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getSearchedUsers(nickname:String,lastUserId:Int?): Flow<Resource<Users>>
    suspend fun toggleFollowUser(userId:Int): Flow<Resource<ToggleFollowResult>>
    suspend fun getUserInfo(userId:Int): Flow<Resource<Users>>
    fun getMediaPosts(userId:Int,type:String,latitude:Double?,longitude:Double?):Flow<PagingData<MediaPost>>
}