package com.androiddev.snsappwithcompose.common.base

import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.UiText

sealed class UiEvent {
    data class ShowToast(val message: UiText): UiEvent()
    data class navigate(val screen: Screen, val userId:Int? = null): UiEvent()
    data object popBackStack: UiEvent()
    data class PopBackStackWithResult<T>(val key: String, val value: T) : UiEvent()

    sealed class RecordUiEvent : UiEvent() {
        data class StartRecordService(val action: String) : RecordUiEvent()
        data class SendStatusBroadcast(val stateStr: String, val formattedTime: String) : RecordUiEvent()
    }
}