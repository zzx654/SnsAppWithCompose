package com.androiddev.snsappwithcompose.feature.home.user

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.User
import com.androiddev.domain.model.Users
import com.androiddev.domain.use_case.user.UserUseCases
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.Paginator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userUseCases: UserUseCases
): BaseViewModel(context) {
    private val _nicknameTextField = mutableStateOf("")
    val nicknameTextField: State<String>
        get() = _nicknameTextField
    protected val _getUsersState = mutableStateOf(GetUsersState())
    val getUsersState: State<GetUsersState> get() = _getUsersState
    private val _followUserStatusMap = mutableStateMapOf<Int, Boolean>()
    val followUserStatusMap: Map<Int, Boolean> get() = _followUserStatusMap

    private val _userInfo: MutableState<User?> = mutableStateOf(null)
    val userInfo: State<User?>
        get() = _userInfo
    val userPaginator =
        Paginator<Users, User>(
            loadItems = { handleResult,refresh ->
                viewModelScope.launch {

                    var lastUserId: Int? = null
                    with(getUsersState.value.users) {
                        if (isNotEmpty() && !refresh) {
                            lastUserId = last().userId
                        }
                    }

                    if(nicknameTextField.value.isNotEmpty()) {

                        userUseCases.getSearchedUsers(nicknameTextField.value,lastUserId)
                            .collect {

                                handleResult(it)
                            }
                    }
                }


            },
            onRefreshUpdated = {},
            onLoadUpdated = { isLoading ->
                _getUsersState.value = _getUsersState.value.copy(isLoading = isLoading)

            },
            onError = { message ->
                _getUsersState.value = getUsersState.value.copy(error = message)
            },
            onSuccess = { users,refresh ->
                updateFollowingStateForNewUsers(users)
                _getUsersState.value = getUsersState.value.copy(
                    users = getUsersState.value.users + users,
                    endReached = users.isEmpty() && getUsersState.value.users.isNotEmpty()
                )
            },
            extractItems = { response -> response.users }
        )
    private fun updateFollowingStateForNewUsers(users:List<User>) {
        users.forEach { user ->
            _followUserStatusMap[user.userId] = user.following == 1
        }

    }
    private fun fetchUserInfo(userId:Int) {
        viewModelScope.launch {
            userUseCases.getUserInfo(userId).collect { result ->
                handleResource(
                    resource = result,
                    onSuccess = { data ->

                        _userInfo.value = data.users[0]


                    }
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
                _nicknameTextField.value = event.nickname
                if(event.nickname.isNotEmpty()) {

                    viewModelScope.launch {
                        delay(20L)
                        userPaginator.loadNextItems(refresh = true)
                    }
                } else {
                    _getUsersState.value = getUsersState.value.copy(
                        users = listOf()
                    )
                }
            }
            is UserEvent.LoadNext -> {
                viewModelScope.launch {
                    userPaginator.loadNextItems(refresh = false)
                }
            }
            is UserEvent.ToggleFollowUser -> {
                viewModelScope.launch {
                    userUseCases.toggleFollowUser(event.userId).collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _followUserStatusMap[event.userId] = data.isFollowing
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
