package com.androiddev.snsappwithcompose.common.base


import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BaseScreen(
    viewModel: UiStateProvider,
    navController: NavController? = null, // 필요시 내비게이션 처리
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // 1. 공통 이벤트 수집 (Toast, Navigate 등)
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).also { toast ->
                        toast.setGravity(Gravity.BOTTOM, 0, 130)
                        toast.show()
                    }
                }
                is UiEvent.navigate -> {
                    navController?.navigate(event.screen)
                }

                is UiEvent.popBackStack -> {
                    navController?.popBackStack()
                }
                else -> null
            }
        }
    }

    // 2. 공통 UI 렌더링 (실제 화면 + 로딩 다이얼로그)
    Box(modifier = Modifier.fillMaxSize()) {
        content() // 각 화면 고유의 UI가 들어옴

        LoadingDialog { isLoading }

    }
}