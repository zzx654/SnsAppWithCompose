package com.androiddev.snsappwithcompose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.util.addFocusCleaner

@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier.fillMaxWidth(0.85f),
    focusManager: FocusManager,
    scrollState: ScrollState = rememberScrollState(),
    topBar: @Composable (() -> Unit) = {},
    content: @Composable () -> Unit,
    bottomBar: @Composable (() -> Unit) = {},
    lazyColumnExist: Boolean = false
) {
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                topBar()
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.imePadding()
            ) {
                bottomBar()
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .addFocusCleaner(focusManager)
            .background(MaterialTheme.colorScheme.background),
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { contentPadding ->
        // contentPadding = Scaffold가 제공하는 top/bottom bar 공간 패딩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.then (
                    if(lazyColumnExist) Modifier.fillMaxSize()
                    else Modifier.fillMaxSize().verticalScroll(scrollState)
                ),
                //.fillMaxSize()
                //.verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = modifier
                ) {
                    content()
                }
            }
        }
    }
}