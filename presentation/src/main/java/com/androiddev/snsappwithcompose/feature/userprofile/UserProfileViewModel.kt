package com.androiddev.snsappwithcompose.feature.userprofile

import android.content.Context
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.use_case.user.UserUseCases
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePagingViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userUseCases: UserUseCases,
    private val getPostsUseCases: GetPostsUseCases,
    locationClient: FusedLocationProviderClient,
    savedStateHandle: SavedStateHandle
) : BasePagingViewModel(context,locationClient) {
    val args: Screen.UserProfileScreen = savedStateHandle.toRoute<Screen.UserProfileScreen>()
    val tabs = listOf(
        UserContent.HOME,
        UserContent.IMAGE,
        UserContent.VIDEO
    )


    // 선택된 값 (State)
    private val currentTab =
        MutableStateFlow(UserContent.HOME)

    val selectedTab =
        currentTab.asStateFlow()

    fun selectTab(
        tab: UserContent
    ) {

        currentTab.value = tab

    }
    private var homePager: Flow<PagingData<PostPreview>>? = null
    private val mediaPagerCache =
        mutableMapOf<
                UserContent,
                Flow<PagingData<MediaPost>>
                >()

    fun getHomePosts(): Flow<PagingData<PostPreview>> {
        println("!!!!!!!!!!!!!!!!!!${getLatitude()}")
        return homePager ?: getPostsUseCases.getUserPosts(
            userId = args.userId,
            latitude = getLatitude(),
            longitude = getLongitude()
        ).cachedIn(viewModelScope)
            .also { homePager = it }
    }
    fun getMediaPosts(
        tab: UserContent
    ): Flow<PagingData<MediaPost>> {

        require(tab != UserContent.HOME)

        return mediaPagerCache.getOrPut(tab) {


            userUseCases.getMediaPosts(
                userId = args.userId,
                type = tab.name,
                latitude = getLatitude(),
                longitude = getLongitude()
            ).cachedIn(viewModelScope)

        }
    }

    /**@OptIn(ExperimentalCoroutinesApi::class)
    val media =
        currentTab
            .filter { it != UserContent.HOME }
            .flatMapLatest { tab ->

                pagerCache.getOrPut(tab) {

                    userUseCases.getMediaPosts(
                        userId = args.userId,
                        type = tab.name,
                        latitude = getLatitude(),
                        longitude = getLongitude()
                    ).cachedIn(viewModelScope)

                }
            }**/

    /**@OptIn(ExperimentalCoroutinesApi::class)
    val media =

        currentTab.flatMapLatest { tab ->

            pagerCache.getOrPut(tab) {

                userUseCases.getMediaPosts(


                    userId = args.userId,

                    type = tab.name,
                    latitude = getLatitude(),
                    longitude = getLongitude()
                ).cachedIn(viewModelScope)

            }

        }**/




}
enum class UserContent {
    HOME,IMAGE,VIDEO

}