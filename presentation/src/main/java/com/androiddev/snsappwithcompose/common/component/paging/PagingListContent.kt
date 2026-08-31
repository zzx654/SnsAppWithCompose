package com.androiddev.snsappwithcompose.common.component.paging

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.androiddev.snsappwithcompose.R

@Composable
fun <T : Any> PagingListContent(
    items: LazyPagingItems<T>,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    keyExtractor: ((T) -> Any)? = null, // Key 지정으로 리컴포지션 최적화
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    showDivider:Boolean = true,
    dividerContent: @Composable () -> Unit = {
        HorizontalDivider(
            //modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 4.dp,
            color = Color.LightGray.copy(0.25f)
        )
    },
    emptyContent: @Composable () -> Unit = { DefaultEmptyView(getString(LocalContext.current,R.string.nodata_to_display)) },
    canRefresh:Boolean = true,
    additionalHeader: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isManualRefreshing by remember { mutableStateOf(false) }


    //직접 당겼을 때 + 로딩 중일 때 + 기존 아이템이 있을 때만 상단 인디케이터 표시
    val isRefreshing = isManualRefreshing &&
            (items.loadState.refresh is LoadState.Loading) &&
            items.itemCount > 0

    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(items.loadState.refresh) {
        if (isManualRefreshing && items.loadState.refresh is LoadState.NotLoading) {
            // 로딩이 성공적으로 끝났을 때만 최상단으로 스크롤
            listState.animateScrollToItem(0)
            // 스크롤 이동 후 플래그 해제
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
                },
                enabled = canRefresh
            )
    ) {
        //첫 페이지 로딩 중일 때
        if (items.loadState.refresh is LoadState.Loading && items.itemCount == 0) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp),color = Color.Gray)
        }
        //첫 페이지 로딩 실패했을 때
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
        // 로딩 완료 후 데이터가 비어있을 때
        else if (items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0) {
            emptyContent()
        }
        // 리스트 표시
        else {
            LazyColumn(
                state = listState,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    additionalHeader?.invoke()
                }
                items(
                    count = items.itemCount,
                    key = items.itemKey { item ->
                        keyExtractor?.invoke(item) ?: item.hashCode()
                    }
                ) { index ->
                    val item = items[index]
                    if (item != null) {
                        itemContent(item)

                        if (showDivider && index < items.itemCount - 1) {
                            dividerContent()
                        }
                    }
                }

                // 다음 페이지(추가 데이터) 로딩 중 처리 (Append)
                when (items.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter),color = Color.Gray)
                            }
                        }
                    }
                    is LoadState.Error -> {
                        val error = (items.loadState.append as LoadState.Error).error
                        item {
                            DefaultErrorView(
                                message = error.localizedMessage ?: getString(context,R.string.failed_to_loadmore),
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

// 기본 에러 뷰
@Composable
fun DefaultErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context
) {
    Box(modifier = modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                tint = Color.DarkGray.copy(0.8f),
            )
            Text(
                text = message,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onRetry() }.padding(top= 5.dp),
                textAlign = TextAlign.Center
            )

        }

    }
}

// 기본 빈 화면 뷰
@Composable
fun DefaultEmptyView(emptyMessage:String,modifier:Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize().height(100.dp), contentAlignment = Alignment.Center) {
        Text(text = emptyMessage)
    }
}