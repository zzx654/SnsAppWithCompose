package com.androiddev.snsappwithcompose.common.component.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems


@Composable
fun <T : Any> PagingScreen(
    pagingItems: LazyPagingItems<T>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    canRefresh: Boolean = true,
    onRefresh: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val refreshState = pagingItems.loadState.refresh
    val itemCount = pagingItems.itemCount
    val isInitialLoading = refreshState is LoadState.Loading && itemCount == 0

    val pullToRefreshState = rememberPullToRefreshState()

    var isManualRefreshing by remember { mutableStateOf(false) }


    LaunchedEffect(refreshState) {
        if (refreshState !is LoadState.Loading) {
            isManualRefreshing = false
        }
    }
    when {
        // 최초 데이터 로딩 중일 때만 전체 로딩 UI 표시
        isInitialLoading -> {
            PagingLoading()
        }

        // 에러 발생 시 에러 UI 표시
        refreshState is LoadState.Error -> {
            PagingError(onRetry = { pagingItems.retry() })
        }

        // 로딩 완료 후 데이터가 없을 때
        itemCount == 0 -> {
            PagingEmpty(emptyMessage)
        }

        else -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        state = pullToRefreshState,

                        isRefreshing = isManualRefreshing,
                        onRefresh = {
                            isManualRefreshing = true
                            onRefresh()
                            pagingItems.refresh()
                        },
                        enabled = canRefresh
                    )
            ) {
                content()

                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isManualRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}