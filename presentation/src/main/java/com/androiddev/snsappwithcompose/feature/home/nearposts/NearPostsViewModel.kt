package com.androiddev.snsappwithcompose.feature.home.nearposts

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePagingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class NearPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    locationProvider: LocationProvider,
    @ApplicationContext context: Context
): BasePagingViewModel(
    context = context,
    locationProvider = locationProvider,
    isLocationPermissionRequired = true
) {

    private val _distance = MutableStateFlow(5)

    val distance: StateFlow<Int> = _distance.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val nearPosts = combine(
        location.filterNotNull(),
        _distance
    ) { loc, dist ->

        Pair(loc, dist)
    }.flatMapLatest { (loc, dist) ->

        getPostsUseCases.getNearPosts(
            maxDistance = dist,
            latitude = loc.latitude ?: 0.0,
            longitude = loc.longitude ?: 0.0
        )
    }.cachedIn(viewModelScope)

    fun setDistance(distance:Int) {
        _distance.value = distance
    }

}