package com.androiddev.snsappwithcompose.common.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    // 단순 String 문자열 (서버에서 온 에러 메시지 등)
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    // Composable 환경에서 바로 String 추출
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    // 일반 Context 환경(Toast 등)에서 String 추출
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}