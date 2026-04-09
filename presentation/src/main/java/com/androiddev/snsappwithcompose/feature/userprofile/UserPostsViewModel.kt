package com.androiddev.snsappwithcompose.feature.userprofile

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Posts
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePostsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    locationClient: FusedLocationProviderClient,
    @ApplicationContext context: Context,
): BasePostsViewModel(
    context = context,
    locationClient = locationClient
) {

    private val _userId: MutableState<Int?> = mutableStateOf(null)
    val userId: State<Int?>
        get() = _userId


    fun initUserPosts(userId:Int) {
        _userId.value = userId
        viewModelScope.launch {
            postPaginator.loadNextItems(refresh = false)
        }

    }

    override fun fetchPostsWithLocation(
        latitude: Double?,
        longitude: Double?,
        refresh: Boolean,
        handleResult: suspend (Resource<Posts>) -> Unit
    ) {
        var lastPostId:Int? = null
        var lastPostDate:String? = null
        with(getPostState.value.posts) {
            if(isNotEmpty()&&!refresh) {
                lastPostDate = last().date
                lastPostId = last().postId
            }
        }
        viewModelScope.launch {

            getPostsUseCases.getUserPosts(
                userId = userId.value?:0,
                postId = lastPostId,
                postDate = lastPostDate,
                latitude = latitude,
                longitude = longitude,
            ).collect { result ->
                handleResult(result)
            }

        }
    }
}