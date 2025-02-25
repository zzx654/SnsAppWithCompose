package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.util.addFocusCleaner

@Composable
fun ScreenWithTopBar(
    focusManager: FocusManager,
    topBar: @Composable (() -> Unit),
    content: @Composable (() -> Unit),
    bottomBar: @Composable (() -> Unit) = {}
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                topBar()
            }
        },
        modifier = Modifier.fillMaxSize().addFocusCleaner(focusManager)
    ) { contentPadding ->
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.85f)
                    .padding(contentPadding)
                    .verticalScroll(scrollState)) {
                content()
            }
            bottomBar()
        }
    }
}