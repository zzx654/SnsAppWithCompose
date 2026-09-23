package com.androiddev.domain.use_case.postlist

import androidx.paging.PagingData
import com.androiddev.domain.location.LocationTracker
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.repository.postlist.PostListRepository
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**class GetUserPosts @Inject constructor(
    private val repository: GetPostsRepository
){

    operator fun invoke(
        userId:Int,
        latitude:Double? = null,
        longitude:Double? = null
    ): Flow<PagingData<PostPreview>> = repository.getUserPosts(
        userId = userId,
        latitude = latitude,
        longitude = longitude
    )
}**/
class GetUserPosts @Inject constructor(
    private val repository: PostListRepository,
    private val locationTracker: LocationTracker
) {
    suspend operator fun invoke(userId:Int):Flow<PagingData<Post>> {
        val locationState = locationTracker.updateLocation()
        return repository.getUserPosts(userId = userId,locationState = locationState)
    }
}