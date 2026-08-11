package com.androiddev.snsappwithcompose.common.component.paging

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.androiddev.snsappwithcompose.R

@Composable
fun <T : Any> PagingGridContent(
    items: LazyPagingItems<T>,
    itemContent: @Composable (T, Int) -> Unit,
    modifier: Modifier = Modifier,
    columns: GridCells = GridCells.Fixed(2),
    keyExtractor: ((T) -> Any)? = null,
    gridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(1.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp),
    emptyContent: @Composable () -> Unit = {
        DefaultEmptyView(getString(LocalContext.current, R.string.nodata_to_display))
    },
    canRefresh:Boolean = true,
    onRefreshExtra: (() -> Unit)? = null // 💡 프로필 UI 정보 등 추가 refresh가 필요한 경우
) {
    val context = LocalContext.current
    var isManualRefreshing by remember { mutableStateOf(false) }

    val isRefreshing = isManualRefreshing &&
            (items.loadState.refresh is LoadState.Loading) &&
            items.itemCount > 0

    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(items.loadState.refresh) {
        if (isManualRefreshing && items.loadState.refresh is LoadState.NotLoading) {
            gridState.animateScrollToItem(0)
            isManualRefreshing = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    isManualRefreshing = true
                    items.refresh()
                    onRefreshExtra?.invoke()
                },
                enabled = canRefresh
            )
    ) {
        // 1. 첫 페이지 로딩 중
        if (items.loadState.refresh is LoadState.Loading && items.itemCount == 0) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                color = Color.Gray
            )
        }
        // 2. 첫 페이지 로딩 실패
        else if (items.loadState.refresh is LoadState.Error) {
            isManualRefreshing = false
            val error = (items.loadState.refresh as LoadState.Error).error
            DefaultErrorView(
                message = error.localizedMessage ?: getString(context, R.string.data_load_failed),
                onRetry = { items.retry() },
                modifier = Modifier.align(Alignment.Center),
                context = context
            )
        }
        // 3. 데이터가 없을 때
        else if (items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0) {
            emptyContent()
        }
        // 4. 그리드 리스트 표시
        else {
            LazyVerticalGrid(
                columns = columns,
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey { item ->
                        keyExtractor?.invoke(item) ?: item.hashCode()
                    }
                ) { index ->
                    val item = items[index]
                    if (item != null) {
                        itemContent(item, index)
                    }
                }

                // 다음 페이지 추가 로딩 (Append)
                when (items.loadState.append) {
                    is LoadState.Loading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.Gray)
                            }
                        }
                    }
                    is LoadState.Error -> {
                        val error = (items.loadState.append as LoadState.Error).error
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            DefaultErrorView(
                                message = error.localizedMessage ?: getString(context, R.string.failed_to_loadmore),
                                onRetry = { items.retry() },
                                context = context
                            )
                        }
                    }
                    else -> {}
                }
            }
        }

        PullToRefreshDefaults.Indicator(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}