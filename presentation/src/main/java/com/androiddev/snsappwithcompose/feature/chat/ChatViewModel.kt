package com.androiddev.snsappwithcompose.feature.chat

import android.util.Log
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.use_case.chat.ChatUseCases
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

object ChatRequestEventBus {
    private val _events = MutableSharedFlow<ChatRequest>(
        extraBufferCapacity = 64 // 코루틴 없이 tryEmit을 쓰기 위한 버퍼 용량
    )
    val events: SharedFlow<ChatRequest> = _events.asSharedFlow()

    fun emit(item: ChatRequest) {
        Log.d("emittest", "emit success: $item")
        // CoroutineScope 생성 없이 즉시 방출! (구독 중인 ViewModel이 있으면 수신, 없으면 안전하게 통과)
        _events.tryEmit(item)
    }
}
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
): BaseViewModel() {

    // 앱 실행 중 FCM으로 들어온 실시간 채팅요청 목록
    private val _fcmChatRequests = MutableStateFlow<List<ChatRequest>>(emptyList())
    val fcmChatRequests: StateFlow<List<ChatRequest>> = _fcmChatRequests.asStateFlow()

}