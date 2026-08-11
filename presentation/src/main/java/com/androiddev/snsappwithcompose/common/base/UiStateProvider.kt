package com.androiddev.snsappwithcompose.common.base

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UiStateProvider {
    val isLoading: StateFlow<Boolean>
    val eventFlow: SharedFlow<UiEvent>
}