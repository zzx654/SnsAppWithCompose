package com.androiddev.snsappwithcompose.feature.notification


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import com.androiddev.snsappwithcompose.common.component.AlertDialogg
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import com.androiddev.snsappwithcompose.feature.notification.component.NotificationItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel
) {

    val notificationItems = viewModel.pagingDataStream.collectAsLazyPagingItems()
    val fcmList by viewModel.fcmNotifications.collectAsStateWithLifecycle()

    var isInitialLoadCompleted by remember { mutableStateOf(false) }
    val readIds by viewModel.readIds.collectAsStateWithLifecycle()
    val lastReadMaxId by viewModel.lastReadMaxId.collectAsStateWithLifecycle()
    val lastDeletedMaxId by viewModel.lastDeletedMaxId.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val alertDialogState by viewModel.alertDialogState.collectAsStateWithLifecycle()
    LaunchedEffect(notificationItems.loadState.refresh) {
        val refreshState = notificationItems.loadState.refresh


        if (refreshState is LoadState.NotLoading) {
            if (!isInitialLoadCompleted) {
                isInitialLoadCompleted = true
            }
            viewModel.onRefreshSuccess()
        }
    }

   var lastObservedFcmSize by rememberSaveable { mutableIntStateOf(fcmList.size) }
    LaunchedEffect(fcmList.size) {
        val currentSize = fcmList.size

        if (currentSize > lastObservedFcmSize && currentSize > 0) {
            listState.scrollToItem(0)
        }

        lastObservedFcmSize = currentSize
    }

// [백그라운드/다른 탭 복귀] 복귀 시점에 '이전보다 알림 개수가 늘어났을 때만' 최상단 스크롤
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 다른 탭/화면에서 돌아왔을 때 신규 알림이 실제로 늘어난 경우에만 최상단 이동
                if (fcmList.size > lastObservedFcmSize && fcmList.isNotEmpty()) {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                    lastObservedFcmSize = fcmList.size
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
                    listState = listState,
                    keyExtractor = { it.id },
                    itemContent = { item ->
                        if (item.id > lastDeletedMaxId) {
                            val isRead = item.isRead ||
                                    (item.id in readIds) ||
                                    (item.id <= lastReadMaxId)

                            NotificationItem(
                                notification = item.copy(isRead = isRead),
                                onNotificationClick = {
                                    viewModel.onEvent(NotificationEvent.ReadNotification(item))
                                }
                            )
                        }
                    },
                    dividerContent = { item ->
                        if (item.id > lastDeletedMaxId) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray
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

