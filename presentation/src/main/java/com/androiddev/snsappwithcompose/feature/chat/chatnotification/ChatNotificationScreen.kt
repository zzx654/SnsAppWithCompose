package com.androiddev.snsappwithcompose.feature.chat.chatnotification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androiddev.snsappwithcompose.feature.chat.ChatViewModel

@Composable
fun ChatNotificationScreen(
    viewModel: ChatViewModel
) {

    val chatRequestsCount by viewModel.chatRequestsCount.collectAsStateWithLifecycle()





}