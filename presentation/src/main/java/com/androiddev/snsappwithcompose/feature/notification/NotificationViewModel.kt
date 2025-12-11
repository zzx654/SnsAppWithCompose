package com.androiddev.snsappwithcompose.feature.notification

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.use_case.notification.NotificationUseCases
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.util.Paginator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    notificationUseCases: NotificationUseCases,
    @ApplicationContext context: Context,
) : BaseViewModel(context) {
    private val _getNotificationsState = mutableStateOf(GetNotificationsState())
    val getNotificationsState: State<GetNotificationsState>
        get() = _getNotificationsState
    private val _hasNewNotification = mutableStateOf(false)
    val hasNewNotification:State<Boolean>
        get() = _hasNewNotification
    val notificationPaginator =
        Paginator<List<NotificationItem>,NotificationItem>(
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
            extractItems = { response -> response }
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
        }
    }

   // fun clearBadge() {
   //     _hasNewNotification.value = false
  //  }
}