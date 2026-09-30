package com.androiddev.snsappwithcompose.feature.userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.use_case.user.UserUseCases
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.feature.home.BasePostsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val getPostsUseCases: GetPostsUseCases,
    savedStateHandle: SavedStateHandle
) :BasePostsViewModel() {
    val args: Screen.UserProfileScreen = savedStateHandle.toRoute<Screen.UserProfileScreen>()
    val tabs = listOf(
        UserContent.HOME,
        UserContent.IMAGE,
        UserContent.VIDEO
    )
    val homePostsDataStream
        = getPostsUseCases.getUserPosts(args.userId).cachedIn(viewModelScope)
    private val imagePostsDataStream: Flow<PagingData<MediaPost>> by lazy {
        userUseCases.getMediaPosts(
            userId = args.userId,
            type = UserContent.IMAGE.name
        ).cachedIn(viewModelScope)
    }

    private val videoPostsDataStream: Flow<PagingData<MediaPost>> by lazy {
        userUseCases.getMediaPosts(
            userId = args.userId,
            type = UserContent.VIDEO.name
        ).cachedIn(viewModelScope)
    }

    fun getMediaPosts(tab: UserContent): Flow<PagingData<MediaPost>> {
        return when (tab) {
            UserContent.IMAGE -> imagePostsDataStream
            UserContent.VIDEO -> videoPostsDataStream
            UserContent.HOME -> error("HOME 탭은 MediaPosts를 지원하지 않습니다.")
        }
    }

}
enum class UserContent {
    HOME,IMAGE,VIDEO

}