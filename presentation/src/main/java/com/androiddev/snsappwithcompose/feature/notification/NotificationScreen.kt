package com.androiddev.snsappwithcompose.feature.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.feature.notification.component.NotificationItem

@Composable
fun NotificationScreen(
    navController: NavController,
) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
   // val notifications by notificationViewModel.notifications.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyColumn {
            items(10) { index ->
                NotificationItem(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}
