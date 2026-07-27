package com.androiddev.snsappwithcompose.feature.home.newPosts

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.model.Posts
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePagingViewModel
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePostsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    locationProvider: LocationProvider,
    @ApplicationContext context: Context
): BasePagingViewModel(
    context = context,
    locationProvider = locationProvider
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val newPosts =
        location
            .filterNotNull()
            .flatMapLatest { location ->

                getPostsUseCases.getNewPosts(
                    latitude = location.latitude,
                    longitude = location.longitude
                )

            }
            .cachedIn(viewModelScope)
}