package com.androiddev.snsappwithcompose.feature.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import com.androiddev.domain.util.Constants.PAGE_SIZE
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.component.AlertDialogg
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItemm
import com.androiddev.snsappwithcompose.feature.notification.component.NotificationItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel
) {
    val notificationItems = viewModel.pagingDataStream.collectAsLazyPagingItems()
    val fcmList by viewModel.fcmNotifications.collectAsStateWithLifecycle()
    val readIds by viewModel.readIds.collectAsStateWithLifecycle()
    val lastReadMaxId by viewModel.lastReadMaxId.collectAsStateWithLifecycle()
    val lastDeletedMaxId by viewModel.lastDeletedMaxId.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val alertDialogState by viewModel.alertDialogState.collectAsStateWithLifecycle()
    LaunchedEffect(notificationItems.loadState.refresh) {
        val refreshState = notificationItems.loadState.refresh


        if (refreshState is LoadState.NotLoading) {
            viewModel.onRefreshSuccess()
        }
    }
    AlertDialogg (
        title = alertDialogState.title?.asString() ?:"",
        cancelText = alertDialogState.cancelText?.asString() ?:"",
        confirmText = alertDialogState.confirmText?.asString() ?:"",
        onClickConfirm = alertDialogState.onClickConfirm,
        onClickCancel = alertDialogState.onClickCancel
    )
    LoadingDialog { isLoading }

        BaseScaffold(
            modifier = Modifier.fillMaxWidth(),
            focusManager = focusManager,
            scrollState = scrollState,
            topBar = {
                CenterAlignedTopBar(
                    title = stringResource(R.string.notification),
                    leftAction = {
                        IconButton(onClick = {
                            val topNotificationId = notificationItems.itemSnapshotList.items.firstOrNull()?.id
                            viewModel.onEvent(NotificationEvent.ReadAllNotifications(topNotificationId))
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }

                    },
                    rightAction = {
                        IconButton(onClick = {
                            val topNotificationId = notificationItems.itemSnapshotList.items.firstOrNull()?.id
                            viewModel.onEvent(NotificationEvent.DeleteNotifications(topNotificationId))
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = null
                            )
                        }

                    }
                )
            },
            content = {
                PagingListContent(
                    items = notificationItems,
                    keyExtractor = { it.id },

                    // 1. FCM 실시간 알림 목록
                    additionalHeader = {
                        if (fcmList.isNotEmpty()) {
                            Column {
                                fcmList.forEach { fcmItem ->
                                    if (fcmItem.id > lastDeletedMaxId) {
                                        val isRead = fcmItem.isRead ||
                                                (fcmItem.id in readIds) ||
                                                (fcmItem.id <= lastReadMaxId)

                                        NotificationItem(
                                            notification = fcmItem.copy(isRead = isRead),
                                            onNotificationClick = {
                                                viewModel.onEvent(NotificationEvent.ReadNotification(fcmItem))
                                            }
                                        )

                                        HorizontalDivider(
                                            thickness = 4.dp,
                                            color = Color.LightGray.copy(0.25f)
                                        )
                                    }
                                }
                            }
                        }
                    },

                    itemContent = { item ->
                        if (item.id > lastDeletedMaxId) {
                            val isRead = item.isRead ||
                                    (item.id in readIds) ||
                                    (item.id <= lastReadMaxId)

                            android.util.Log.d(
                                "ReadCheck",
                                "아이템ID: ${item.id} | readIds내용: $readIds | readIds에 있음?: ${item.id in readIds} | 최종isRead: $isRead"
                            )

                            NotificationItem(
                                notification = item.copy(isRead = isRead),
                                onNotificationClick = {
                                    viewModel.onEvent(NotificationEvent.ReadNotification(item))
                                }
                            )
                        }
                    },

                    // 당겨서 새로고침(Refresh) 시 FCM 임시 목록 정리 등의 액션 전달
                    onRefresh = {
                        viewModel.onRefreshStart()

                    }
                )


            },
            lazyColumnExist = true
        )




}
