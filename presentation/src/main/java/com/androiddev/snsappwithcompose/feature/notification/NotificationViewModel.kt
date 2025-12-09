package com.androiddev.snsappwithcompose.feature.notification

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.NotificationItem
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
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
    @ApplicationContext context: Context,
) : BaseViewModel(context) {
    private val _getNotificationsState = mutableStateOf(GetNotificationsState())
    val getNotificationsState: State<GetNotificationsState>
        get() = _getNotificationsState
    private val _notifications: MutableState<List<NotificationItem>> = mutableStateOf(listOf())
    val notifications: State<List<NotificationItem>>
        get() = _notifications
    private val _hasNewNotification = mutableStateOf(false)
    val hasNewNotification:State<Boolean>
        get() = _hasNewNotification


    init {

        viewModelScope.launch {
            NotificationEventBus.events.collect {
                addNotification(it)
            }
        }
    }

    private fun loadInitialNotifications() {
       // viewModelScope.launch {
       //     val items = repository.fetchNotifications()  // API 호출
       //     _notifications.value = items
      //  }
    }

    /** FCM 도착 시 호출 */
    fun addNotification(notification:NotificationItem) {
        _getNotificationsState.value = _getNotificationsState.value.copy(
            notifications = listOf(notification) + _getNotificationsState.value.notifications
        )
        _notifications.value = listOf(notification) + _notifications.value
        _hasNewNotification.value = true
    }

   // fun clearBadge() {
   //     _hasNewNotification.value = false
  //  }
}