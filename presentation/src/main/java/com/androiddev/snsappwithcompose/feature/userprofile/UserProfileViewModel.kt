package com.androiddev.snsappwithcompose.feature.userprofile

import android.content.Context
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
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
    /**private val mediaPagerCache =
        mutableMapOf<
                UserContent,
                Flow<PagingData<MediaPost>>
                >()**/


    fun onEvent(event:UserProfileEvent) {
        when(event) {
            is UserProfileEvent.OnClickImageItem -> {
                viewModelScope.launch {
                }
            }
        }
    }
    /**fun getMediaPosts(
        tab: UserContent
    ): Flow<PagingData<MediaPost>> {

        require(tab != UserContent.HOME)

        return mediaPagerCache.getOrPut(tab) {
            userUseCases.getMediaPosts(
                userId = args.userId,
                type = tab.name,
            ).cachedIn(viewModelScope)
        }
    }**/
}
enum class UserContent {
    HOME,IMAGE,VIDEO

}