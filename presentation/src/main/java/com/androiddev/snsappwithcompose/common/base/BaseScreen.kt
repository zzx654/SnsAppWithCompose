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
    navController: NavController? = null,
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
                is UiEvent.PopBackStackWithResult<*> -> {
                    if (navController != null) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(event.key, event.value)
                    }
                    navController?.popBackStack()
                }
                else -> null
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        LoadingDialog { isLoading }

    }
}