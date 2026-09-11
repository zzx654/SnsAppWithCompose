package com.androiddev.snsappwithcompose.feature.notification

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.androiddev.domain.model.DeleteReason
import com.androiddev.domain.model.Notification
import com.androiddev.domain.model.NotificationActionResult
import com.androiddev.domain.model.NotificationExtra
import com.androiddev.domain.use_case.notification.NotificationUseCases
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.COMMENT
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.FOLLOW
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.LIKECOMMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.LIKEPOST
import com.androiddev.snsappwithcompose.feature.notification.NotificationType.REPLY
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

object NotificationEventBus {
    private val _events = MutableSharedFlow<Notification>()
    val events: SharedFlow<Notification> = _events


    fun emit(item: Notification) {
        Log.d("emittest", "emit success: $item")
        CoroutineScope(Dispatchers.IO).launch {


          _events.emit(item)
         }
    }

}
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUseCases: NotificationUseCases,
) : BaseViewModel() {

    private val _pending = MutableStateFlow<PendingNotification?>(null)
    val pending: StateFlow<PendingNotification?> = _pending.asStateFlow()
    private val _isUserRefreshing = MutableStateFlow(false)
    // 서버에서 내려준 최근 안 읽은 알림 수 (독립 init/API 연동)
    private val _serverUnreadCount = MutableStateFlow(0)
    val serverUnreadCount: StateFlow<Int> = _serverUnreadCount.asStateFlow()

    // 앱 실행 중 FCM으로 들어온 실시간 알림 목록
    private val _fcmNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val fcmNotifications: StateFlow<List<Notification>> = _fcmNotifications.asStateFlow()

    // 단일 항목 읽음 처리 ID 세트
    private val _readIds = MutableStateFlow<Set<Long>>(emptySet())
    val readIds: StateFlow<Set<Long>> = _readIds.asStateFlow()

    // 전체 읽음 / 삭제 기준 Max ID
    private val _lastReadMaxId = MutableStateFlow(0L)
    val lastReadMaxId: StateFlow<Long> = _lastReadMaxId.asStateFlow()

    private val _lastDeletedMaxId = MutableStateFlow(0L)
    val lastDeletedMaxId: StateFlow<Long> = _lastDeletedMaxId.asStateFlow()

    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()
    private var refreshStartMaxFcmId: Long = 0L
    // PagingData 스트림을 가장 가볍고 순수하게 유지 (UI단 State Binding 방식)
    val pagingDataStream: Flow<PagingData<Notification>> =
        notificationUseCases.getNotifications().cachedIn(viewModelScope)

    // hasUnreadNotification 계산 시 deletedMaxId까지 포함하여 보완
    val hasUnreadNotification: StateFlow<Boolean> = combine(
        _serverUnreadCount,
        _fcmNotifications,
        _readIds,
        _lastReadMaxId,
        _lastDeletedMaxId
    ) { serverUnread, fcmList, readIds, readMaxId, deletedMaxId ->
        // FCM 수신 알림 중 안 읽었고 삭제되지 않은 알림 존재 여부
        val hasUnreadFcm = fcmList.any { fcm ->
            fcm.id > deletedMaxId && fcm.id > readMaxId && fcm.id !in readIds
        }
        Log.d("unread확인", "hasUnreadFcm: ${hasUnreadFcm}, serverunread: ${serverUnread}")

        // 서버 unreadCount가 남아있거나 안 읽은 FCM이 있으면 true
        (serverUnread > 0) || hasUnreadFcm
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        fetchUnreadCount()
        viewModelScope.launch {
            NotificationEventBus.events.collect {
                addNotification(it)
            }
        }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            notificationUseCases.getUnreadNotificationCount().collect { result ->
                result.handle(
                    onLoading = {},
                    onSuccess = { count ->
                        _serverUnreadCount.value = count
                    }
                )
            }
        }
    }

    // Pending 알림 저장 및 소비
    fun setPending(pendingNotification: PendingNotification) {
        _pending.value = pendingNotification
    }

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
    fun addNotification(notification: Notification) {
        Log.d("FcmDebug", "FCM 추가 전 개수: ${_fcmNotifications.value.size}")
        _fcmNotifications.update { currentList ->
            // 중복 방지 후 최신순 추가
            if (currentList.none { it.id == notification.id }) {
                listOf(notification) + currentList
            } else currentList
        }
        Log.d("fcm객체","FcmNotification:${notification},FcmList:${_fcmNotifications.value}")

        Log.d("FcmDebug", "FCM 추가 후 개수: ${_fcmNotifications.value.size}")
    }

    /** 당겨서 새로고침(Refresh) 성공 시 UI/Paging3 단에서 호출 */
    fun onRefreshSuccess() {

        if (_isUserRefreshing.value) {
            //_readIds.value = emptySet()

            _fcmNotifications.update { currentList ->
                currentList.filter { fcm -> fcm.id > refreshStartMaxFcmId }
            }
            _lastReadMaxId.value = 0L
            _lastDeletedMaxId.value = 0L

            _isUserRefreshing.value = false // 플래그 리셋
            refreshStartMaxFcmId = 0L // 스냅샷 초기화
        }
    }


    fun onRefreshStart() {
        Log.d("FcmDebug", ">>> onRefreshStart() 호출됨!")
        refreshStartMaxFcmId = _fcmNotifications.value.maxOfOrNull { it.id } ?: 0L
        fetchUnreadCount()
        _isUserRefreshing.value = true
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.ReadAllNotifications -> {
                showReadAllNotificationAlert(event.targetMaxId)
            }
            is NotificationEvent.DeleteNotifications -> {
                showDeleteNotificationAlert(event.targetMaxId)
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

    private fun readNotification(notificationId: Long, type: String, extraJson: NotificationExtra) {
        viewModelScope.launch {
            notificationUseCases.readNotification(notificationId).collect { result ->
                result.handle(
                    onSuccess = { data ->
                        _readIds.update { it + notificationId }
                        val isFcmItem = _fcmNotifications.value.any { it.id == notificationId }

                        if (!isFcmItem) {
                            // 서버에서 로드된 알림을 읽었을 때만 serverUnreadCount 차감
                            _serverUnreadCount.update { count -> maxOf(0, count - 1) }
                        }
                        when (data.notificationActionResult) {
                            is NotificationActionResult.Navigate -> {
                                val commentId = extraJson.commentId
                                val postId = extraJson.postId
                                val followerId = extraJson.followerId
                                when (type) {
                                    LIKEPOST -> {
                                        postId?.let { id ->
                                            setEvent(UiEvent.navigate(Screen.PostDetailScreen(id)))
                                        }
                                    }
                                    COMMENT, REPLY, LIKECOMMENT -> {
                                        if (postId != null && commentId != null) {
                                            setEvent(
                                                UiEvent.navigate(
                                                    Screen.PostDetailScreen(
                                                        postId = postId,
                                                        notificationCommentId = commentId
                                                    )
                                                )
                                            )
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
                                setEvent(
                                    UiEvent.ShowToast(
                                        when ((data.notificationActionResult as NotificationActionResult.TargetDeleted).reason) {
                                            DeleteReason.POST_DELETED -> UiText.StringResource(R.string.alert_deleted_post)
                                            DeleteReason.COMMENT_DELETED -> UiText.StringResource(R.string.alert_deleted_comment)
                                            DeleteReason.REPLY_DELETED -> UiText.StringResource(R.string.alert_deleted_reply)
                                            else -> UiText.StringResource(R.string.alert_deleted_notification)
                                        }
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    private fun showReadAllNotificationAlert(targetMaxId: Long?) {
        targetMaxId?.let { targetId ->
            _alertDialogState.value = AlertDialogStateV2(
                title = UiText.StringResource(R.string.read_all_notification),
                cancelText = UiText.StringResource(R.string.cancel),
                confirmText = UiText.StringResource(R.string.confirm),
                onClickCancel = { resetDialogState() },
                onClickConfirm = {
                    resetDialogState()
                    viewModelScope.launch {
                        notificationUseCases.readAllNotifications().collect { result ->
                            result.handle(
                                onSuccessUnit = {
                                    _lastReadMaxId.update { maxOf(it, targetId) }
                                    _serverUnreadCount.value = 0
                                }
                            )
                        }
                    }
                }
            )
        }
    }

    private fun showDeleteNotificationAlert(targetMaxId: Long?) {
        targetMaxId?.let { targetId ->
            _alertDialogState.value = AlertDialogStateV2(
                title = UiText.StringResource(R.string.delete_notifications),
                cancelText = UiText.StringResource(R.string.cancel),
                confirmText = UiText.StringResource(R.string.confirm),
                onClickCancel = { resetDialogState() },
                onClickConfirm = {
                    resetDialogState()
                    viewModelScope.launch {
                        notificationUseCases.deleteNotifications().collect { result ->
                            result.handle(
                                onSuccessUnit = {
                                    _lastDeletedMaxId.update { maxOf(it, targetId) }
                                    _serverUnreadCount.value = 0
                                }
                            )
                        }
                    }
                }
            )
        }
    }

    private fun resetDialogState() {
        _alertDialogState.value = AlertDialogStateV2()
    }
}