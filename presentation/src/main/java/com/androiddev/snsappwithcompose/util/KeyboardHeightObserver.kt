package com.androiddev.snsappwithcompose.util

import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import android.view.ViewTreeObserver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

@Composable
fun KeyboardHeightObserver(viewModel: KeyboardViewModel) {
    val view = LocalView.current
    val density = LocalDensity.current

    DisposableEffect(Unit) {


        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val visibleHeight = r.height()

            val keypadHeight = screenHeight - visibleHeight


            // 키보드가 올라왔을 때 기준치 이상인 경우만 처리
            if (keypadHeight > viewModel.keyboardHeight.value) {
                viewModel.setKeyboardHeight(keypadHeight)
            }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}
class KeyboardViewModel : ViewModel() {
    private val _keyboardHeight = mutableStateOf(0)
    val keyboardHeight: State<Int> = _keyboardHeight

    fun setKeyboardHeight(height: Int) {
        _keyboardHeight.value = height
    }
}
