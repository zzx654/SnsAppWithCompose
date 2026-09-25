package com.androiddev.domain.use_case.user

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMediaPosts @Inject constructor(
    private val repository: UserRepository,
    private val locationTracker: LocationTracker
) {
     operator fun invoke(
        userId:Int,
        type:String
    ): Flow<PagingData<MediaPost>> = flow {
        val locationState = locationTracker.updateLocation()
         emitAll(repository.getMediaPosts(
             userId = userId,
             type = type,
             latitude = locationState.latitude,
             longitude = locationState.longitude
         ))

     }
}