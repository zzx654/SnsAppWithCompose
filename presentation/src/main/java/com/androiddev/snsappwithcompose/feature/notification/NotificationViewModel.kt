package com.androiddev.snsappwithcompose.feature.notification

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.DeleteReason
import com.androiddev.domain.model.NotificationActionResult
import com.androiddev.domain.model.NotificationExtra
import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.model.Notifications
import com.androiddev.domain.use_case.notification.NotificationUseCases
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.Paginator
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.COMMENT
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.FOLLOW
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.LIKECOMMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.LIKEPOST
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.REPLY
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NotificationEventBus {
    private val _events = MutableSharedFlow<NotificationItem>()
    val events: SharedFlow<NotificationItem> = _events


    fun emit(item: NotificationItem) {
        Log.d("emittest", "emit success: $item")
        CoroutineScope(Dispatchers.IO).launch {


          _events.emit(item)
         }
    }

}
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUseCases: NotificationUseCases,
    @ApplicationContext context: Context,
) : BaseViewModel(context) {
    private val _pending = MutableStateFlow<PendingNotification?>(null)
    val pending: StateFlow<PendingNotification?> = _pending
    private val _getNotificationsState = mutableStateOf(GetNotificationsState())
    val getNotificationsState: State<GetNotificationsState>
        get() = _getNotificationsState
    private val _hasNewNotification = mutableStateOf(false)
    val hasNewNotification:State<Boolean>
        get() = _hasNewNotification
    private val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState
    val notificationPaginator =
        Paginator<Notifications,NotificationItem>(
            loadItems = { handleResult, refresh ->
                viewModelScope.launch {
                    var lastNotificationId: Long? = null
                    var lastNotificationDate: String? = null
                    with(getNotificationsState.value.notifications) {
                        if (isNotEmpty() && !refresh) {
                            lastNotificationDate = last().date
                            lastNotificationId = last().id
                        }
                    }
                    notificationUseCases.getNotifications(lastNotificationId,lastNotificationDate)
                        .collect {

                            it.data?.let { notificationsInfo ->
                                if(notificationsInfo.notifications.isNotEmpty())
                                    _hasNewNotification.value = notificationsInfo.unreadCount>0
                            }
                            handleResult(it)
                        }
                }
            },
            onRefreshUpdated = { isRefreshing ->
                _getNotificationsState.value =
                    _getNotificationsState.value.copy(isRefreshing = isRefreshing, endReached = false)
            },
            onLoadUpdated = { isLoading ->
                _getNotificationsState.value = _getNotificationsState.value.copy(isLoading = isLoading)

            },
            onError = { message ->
                _getNotificationsState.value = getNotificationsState.value.copy(error = message)

            },
            onSuccess = { notifications, refresh ->

                val newIds = notifications.map { it.id }.toSet()
                _getNotificationsState.value = getNotificationsState.value.copy(
                    notifications = if (refresh) notifications else getNotificationsState.value.notifications.filterNot{ it.id in newIds } + notifications,
                    endReached = notifications.isEmpty() && getNotificationsState.value.notifications.isNotEmpty()
                )

            },
            extractItems = { response -> response.notifications }
        )

    init {
        viewModelScope.launch {
            NotificationEventBus.events.collect {
                addNotification(it)
            }
        }
        viewModelScope.launch {
            notificationPaginator.loadNextItems(refresh = true)
        }
    }
    // Pending 알림 저장
    fun setPending(pendingNotification: PendingNotification) {
        _pending.value = pendingNotification
    }
    // Pending 알림 소비 (navigate 완료 후)
    fun consumePending() {
        pending.value?.let { pend ->
            readNotification(
                notificationId = pend.notificationId,
                type = pend.type,
                extraJson = Gson().fromJson(pend.extraJson, NotificationExtra::class.java)
            )
            _pending.value = null
        }
    }
    /** FCM 도착 시 호출 */
    fun addNotification(notification:NotificationItem) {
        if(getNotificationsState.value.notifications.none{ it.id == notification.id }) {
            _getNotificationsState.value = _getNotificationsState.value.copy(
                notifications = listOf(notification) + _getNotificationsState.value.notifications
            )
        }
        _hasNewNotification.value = true
    }
    fun onEvent(event:NotificationEvent) {
        when(event) {
            is NotificationEvent.LoadNextNotifications -> {
                viewModelScope.launch {
                    notificationPaginator.loadNextItems(refresh = false)
                }
            }
            is NotificationEvent.RefreshNotifictions -> {
                viewModelScope.launch {
                    notificationPaginator.loadNextItems(refresh = true)
                }
            }
            is NotificationEvent.ReadAllNotifications -> {
                showReadAllNotificationAlert()
            }
            is NotificationEvent.DeleteNotifications -> {
                showDeleteNotificationAlert()
            }
            is NotificationEvent.ReadNotification -> {
                readNotification(
                    notificationId = event.notification.id,
                    type = event.notification.type,
                    extraJson = event.notification.extrajson
                )
            }
        }
    }
    private fun readNotification(notificationId:Long,type:String,extraJson:NotificationExtra) {
        viewModelScope.launch {
            notificationUseCases.readNotification(notificationId).collect { result ->

                handleResource(
                    resource = result,
                    onSuccess = { result ->
                        _getNotificationsState.value = getNotificationsState.value.copy(
                            notifications = getNotificationsState.value.notifications.map { notification ->
                                if(notification.id == notificationId) {
                                    notification.copy(isRead = true)
                                } else {
                                    notification
                                }
                            }
                        )
                        result.unreadCount?.let {
                            if(it == 0) _hasNewNotification.value = false
                        }
                        when(result.notificationActionResult) {
                            is NotificationActionResult.Navigate -> {
                                val commentId = extraJson.commentId
                                val postId = extraJson.postId
                                val followerId = extraJson.followerId
                                when(type) {
                                    LIKEPOST-> { //게시물 페이지
                                        postId?.let { id ->
                                            Screen.PostDetailScreen(
                                                id
                                            )
                                        }?.let { screen -> UiEvent.navigate(screen) }
                                            ?.let { setEvent(it) }

                                    }
                                    COMMENT,REPLY,LIKECOMMENT -> { //게시물페이지
                                        if(postId!=null&&commentId!=null) {
                                            setEvent(UiEvent.navigate(
                                                Screen.PostDetailScreen(
                                                    postId = postId,
                                                    notificationCommentId = commentId
                                                )
                                            ))
                                        }
                                    }
                                    FOLLOW -> {
                                        followerId?.let {
                                            setEvent(
                                                UiEvent.navigate(
                                                    Screen.UserProfileScreen(it)
                                                )
                                            )
                                        }

                                    }
                                }

                            }
                            is NotificationActionResult.TargetDeleted -> {
                                setEvent(UiEvent.ShowToast(
                                    when ((result.notificationActionResult as NotificationActionResult.TargetDeleted).reason) {
                                        DeleteReason.POST_DELETED -> "삭제된 게시물입니다"
                                        DeleteReason.COMMENT_DELETED -> "삭제된 댓글입니다"
                                        DeleteReason.REPLY_DELETED -> "삭제된 답글입니다"
                                        else -> "이미 삭제된 알림입니다"
                                    }
                                ))

                            }
                        }
                        //navigate(post(게시물,댓글),reply,profile)
                    }
                )

            }
        }


    }
    private fun showReadAllNotificationAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(R.string.read_all_notification),
            cancelText = getString(R.string.cancel),
            confirmText = getString(R.string.confirm),
            onClickCancel = {
                resetDialogState()
            },
            onClickConfirm = {
                resetDialogState()
                viewModelScope.launch {
                    notificationUseCases.readAllNotifications().collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _hasNewNotification.value = data.unreadCount>0
                                _getNotificationsState.value = getNotificationsState.value.copy(
                                    notifications = getNotificationsState.value.notifications.map{ it.copy(isRead = true)}
                                )
                            }
                        )
                    }
                }
            }
        )
    }
    private fun showDeleteNotificationAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(R.string.delete_notifications),
            cancelText = getString(R.string.cancel),
            confirmText = getString(R.string.confirm),
            onClickCancel = {
                resetDialogState()
            },
            onClickConfirm = {
                resetDialogState()
                viewModelScope.launch {
                    notificationUseCases.deleteNotifications().collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _hasNewNotification.value = data.unreadCount>0
                                _getNotificationsState.value = getNotificationsState.value.copy(
                                    notifications = data.notifications
                                )
                            }
                        )
                    }

                }
            }
        )
    }
    private fun resetDialogState() {
        _alertDialogState.value = AlertDialogState()
    }
}