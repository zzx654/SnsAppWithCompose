package com.androiddev.snsappwithcompose.feature.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

object NotificationEventBus {
    private val _events = MutableSharedFlow<Boolean>()
    val events: SharedFlow<Boolean> = _events

    fun emit(item: Boolean) {
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

   // private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
   // val notifications = _notifications.asStateFlow()

    private val _hasNewNotification = MutableStateFlow(false)
    val hasNewNotification = _hasNewNotification.asStateFlow()


    init {

        viewModelScope.launch {
            NotificationEventBus.events.collect {
                addNotification()
            }
        }

        loadInitialNotifications()
    }

    private fun loadInitialNotifications() {
       // viewModelScope.launch {
       //     val items = repository.fetchNotifications()  // API 호출
       //     _notifications.value = items
      //  }
    }

    /** FCM 도착 시 호출 */
    fun addNotification() {
       // _notifications.value = listOf(item) + _notifications.value
        _hasNewNotification.value = true
    }

   // fun clearBadge() {
   //     _hasNewNotification.value = false
  //  }
}