package com.androiddev.snsappwithcompose.common.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner

fun isAppInForeground(): Boolean {
    // ProcessLifecycleOwner 기준 + Activity 상태 플래그
    val lifecycleForeground = ProcessLifecycleOwner.get()
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    return lifecycleForeground || AppForegroundState.isForeground
}