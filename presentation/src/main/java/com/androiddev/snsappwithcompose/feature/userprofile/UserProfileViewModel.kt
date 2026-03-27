package com.androiddev.snsappwithcompose.feature.userprofile

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    locationClient: FusedLocationProviderClient,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(context) {
    val args: Screen.UserProfileScreen = savedStateHandle.toRoute<Screen.UserProfileScreen>()
    val tabs = listOf(
        UserContent.HOME,
        UserContent.PHOTO,
        UserContent.VIDEO
    )
    init {
        println(args.userId)
    }

    // 선택된 값 (State)
    private val _selectedTab = mutableStateOf(UserContent.HOME)
    val selectedTab: State<UserContent> = _selectedTab

    fun selectTab(tab: UserContent) {
        _selectedTab.value = tab
    }


}
enum class UserContent {
    HOME,PHOTO,VIDEO

}