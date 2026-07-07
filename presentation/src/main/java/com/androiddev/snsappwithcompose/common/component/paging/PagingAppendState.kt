package com.androiddev.snsappwithcompose.common.component.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState

@Composable
fun PagingAppendState(
    loadState: LoadState,
    onRetry: () -> Unit
) {

    when (loadState) {

        is LoadState.Loading -> {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }

        }

        is LoadState.Error -> {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    onClick = onRetry
                ) {
                    Text("다시 시도")
                }

            }

        }

        else -> Unit
    }
}