package com.androiddev.domain.use_case.user

import androidx.paging.PagingData
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaPosts @Inject constructor(
    private val repository: UserRepository
) {
     operator fun invoke(
        userId:Int,
        type:String,
        latitude:Double?,
        longitude:Double?
    ): Flow<PagingData<MediaPost>> = repository.getMediaPosts(
        userId = userId,
        type = type,
        latitude = latitude,
        longitude = longitude
    )
}