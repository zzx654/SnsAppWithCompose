package com.androiddev.snsappwithcompose.feature.home

import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.navigation.component.Screen

abstract class BasePostsViewModel: BaseViewModel() {
    open fun onClickPostItem(postId: Int) {
        emitUiEvent(UiEvent.navigate(Screen.PostDetailScreen(postId)))
    }
}