package com.androiddev.domain.use_case.user

import com.androiddev.domain.model.MediaPosts
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaPosts @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        userId:Int,
        type:String,
        mediaId:Int?,
        latitude:Double?,
        longitude:Double?
    ): Flow<Resource<MediaPosts>> = repository.getMedia(
        userId = userId,
        type = type,
        mediaId = mediaId,
        latitude = latitude,
        longitude = longitude
    )
}