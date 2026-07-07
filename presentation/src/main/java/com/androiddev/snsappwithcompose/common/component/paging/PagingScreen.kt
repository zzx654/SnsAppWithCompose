package com.androiddev.snsappwithcompose.common.component.paging

import androidx.compose.runtime.Composable
import androidx.paging.LoadState

@Composable
fun PagingScreen(
    refreshState: LoadState,
    itemCount: Int,
    onRetry: () -> Unit,
    emptyMessage: String,
    content: @Composable () -> Unit
) {

    when (refreshState) {

        is LoadState.Loading -> {
            PagingLoading()
        }

        is LoadState.Error -> {
            PagingError(onRetry)
        }

        else -> {

            if (itemCount == 0) {
                PagingEmpty(emptyMessage)
            } else {
                content()
            }
        }
    }
}