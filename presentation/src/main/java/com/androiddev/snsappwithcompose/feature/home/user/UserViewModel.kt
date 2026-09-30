package com.androiddev.snsappwithcompose.feature.home.user

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.User
import com.androiddev.domain.use_case.user.UserUseCases
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userUseCases: UserUseCases
): BaseViewModel() {
    private val _nicknameSearchQuery = MutableStateFlow("")
    val nicknameSearchQuery: StateFlow<String>
        get() = _nicknameSearchQuery.asStateFlow()


    private val _userStateMap = MutableStateFlow<Map<Int, User>>(emptyMap())
    val userStateMap: StateFlow<Map<Int, User>> = _userStateMap.asStateFlow()
    private val _userInfo: MutableState<User?> = mutableStateOf(null)
    val userInfo: State<User?>
        get() = _userInfo

    val searchedUsersPagingData: Flow<PagingData<User>> = _nicknameSearchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            val trimmedQuery = query.trim()
            println("DEBUG_SEARCH: flatMapLatest 진입 -> query: '$trimmedQuery'")

            if (trimmedQuery.isEmpty()) {
                println("DEBUG_SEARCH: 빈 쿼리 -> PagingData.empty() 방출")
                flowOf(PagingData.empty())
            } else {
                println("DEBUG_SEARCH: API 호출 진입 -> query: '$trimmedQuery'")
                userUseCases.getSearchedUsers(trimmedQuery)
            }
        }
        .cachedIn(viewModelScope)
    private fun fetchUserInfo(userId:Int) {
        viewModelScope.launch {
            userUseCases.getUserInfo(userId).collect { result ->
                result.handle(
                    onSuccess = { users-> _userInfo.value = users[0] }
                )
            }
        }
    }
    fun refreshUser(userId: Int) {
        onEvent(UserEvent.GetUserInfo(userId))
    }
    fun onEvent(event:UserEvent) {
        when(event) {
            is UserEvent.TypeNickname-> {
                _nicknameSearchQuery.value = event.nickname
            }
            is UserEvent.ToggleFollowUser -> {
                val user = event.user
                val userId = user.userId ?: return
                val currentIsFollowed = user.following == 1
                val targetIsFollowed = !currentIsFollowed

                val updatedUser = user.toggleFollow(isFollowed = targetIsFollowed)
                viewModelScope.launch {
                    userUseCases.toggleFollowUser(userId).collect { result ->
                        result.handle(
                            onSuccess = {
                                _userStateMap.update { currentMap ->
                                    currentMap + (userId to updatedUser)
                                }
                            },
                            onError = {
                                _userStateMap.update { currentMap ->
                                    currentMap + (userId to user) // 원래 상태로 원복
                                }
                            }
                        )
                    }
                }

            }
            is UserEvent.SelectUser -> {
                viewModelScope.launch {
                    setEvent(
                        UiEvent.navigate(
                            Screen.UserProfileScreen(event.userId)
                        )
                    )

                }

            }
            is UserEvent.GetUserInfo -> {
                viewModelScope.launch {
                    fetchUserInfo(event.userId)
                }
            }
        }
    }

}
