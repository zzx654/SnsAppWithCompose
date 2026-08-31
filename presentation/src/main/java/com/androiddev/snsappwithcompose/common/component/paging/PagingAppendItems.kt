package com.androiddev.snsappwithcompose.common.component.paging

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.androiddev.snsappwithcompose.R

fun <T : Any> LazyListScope.pagingAppendItems(
    items: LazyPagingItems<T>,
    context: Context
) {
    when (items.loadState.append) {
        is LoadState.Loading -> {
            item {
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
            item {
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