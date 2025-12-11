package com.androiddev.snsappwithcompose.feature.notification


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.util.Constants.PAGE_SIZE
import com.androiddev.snsappwithcompose.feature.notification.component.NotificationItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val getNotificationsState = viewModel.getNotificationsState.value
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.getNotificationsState.value.isRefreshing,
        onRefresh = {
            viewModel.onEvent(NotificationEvent.RefreshNotifictions)
        }
    )
    BaseScaffold(
        modifier = Modifier.fillMaxWidth(),
        focusManager = focusManager,
        scrollState = scrollState,
        topBar = {
            CenterAlignedTopBar(
                title = getString(context, R.string.notification),
                leftAction = {
                    IconButton(onClick = {


                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }

                },
                rightAction = {
                    IconButton(onClick = {


                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }

                }
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(   getNotificationsState.notifications.size
                    ) { index ->
                        if(index >= getNotificationsState.notifications.size - 1 && !getNotificationsState.endReached && !getNotificationsState.isLoading) {
                            viewModel.onEvent(NotificationEvent.LoadNextNotifications)
                        }
                        NotificationItem(
                            notification = getNotificationsState.notifications[index]
                        )
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                    item {
                        if(getNotificationsState.isLoading && getNotificationsState.notifications.isNotEmpty()
                            &&getNotificationsState.notifications.size >= PAGE_SIZE
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                }
                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    refreshing = viewModel.getNotificationsState.value.isRefreshing,
                    state = pullRefreshState
                )
            }


        },
        lazyColumnExist = true
    )

}
