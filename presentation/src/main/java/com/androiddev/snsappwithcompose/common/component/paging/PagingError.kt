package com.androiddev.snsappwithcompose.common.component.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R

@Composable
fun PagingError(
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Button(
            onClick = onRetry
        ) {

            Text(getString(LocalContext.current, R.string.retry))

        }

    }

}