package com.androiddev.snsappwithcompose.feature.chat

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.model.Notification
import com.androiddev.domain.use_case.chat.ChatUseCases
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.feature.notification.NotificationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    // 페이징 여부 확인
    // 채팅요청목록 개수 상태
    // 채팅수락 id
    //채팅거절 id

    private val basePagingFlow: Flow<PagingData<ChatRequest>> =
        chatUseCases.getChatRequests().cachedIn(viewModelScope)
    // 앱 실행 중 FCM으로 들어온 실시간 채팅요청 목록
    private val _fcmChatRequests = MutableStateFlow<List<ChatRequest>>(emptyList())
    val fcmChatRequests: StateFlow<List<ChatRequest>> = _fcmChatRequests.asStateFlow()

    private val _serverChatRequestsCount = MutableStateFlow(0)
    val serverChatRequestsCount: StateFlow<Int> = _serverChatRequestsCount.asStateFlow()

    private val _acceptedIds = MutableStateFlow<Set<Long>>(emptySet())
    val acceptedIds: StateFlow<Set<Long>> = _acceptedIds.asStateFlow()

    private val _declinedIds = MutableStateFlow<Set<Long>>(emptySet())
    val declinedIds: StateFlow<Set<Long>> = _declinedIds.asStateFlow()
    val pagingDataStream: Flow<PagingData<ChatRequest>> = combine(
        basePagingFlow,
        _fcmChatRequests
    ) { pagingData, fcmList ->
        if (fcmList.isEmpty()) {
            pagingData
        } else {
            val fcmIds = fcmList.map { it.id }.toSet()

            // 1. PagingData 내에서 FCM 아이템과 중복되는 ID 제거
            var resultPagingData = pagingData.filter { chatRequest ->
                chatRequest.id !in fcmIds
            }

            // 2. FCM 아이템들을 상단에 순차적으로 삽입 (최신순)
            fcmList.reversed().forEach { fcmItem ->
                resultPagingData = resultPagingData.insertHeaderItem(item = fcmItem)
            }

            resultPagingData
        }
    }.cachedIn(viewModelScope)

    val chatRequestsCount: StateFlow<Int> = combine(
        _serverChatRequestsCount,
        _fcmChatRequests,
        _acceptedIds,
        _declinedIds
    ) { serverChatRequest, fcmChatRequests, acceptedIds, declinedIds ->

        val hasFcmChatRequests = fcmChatRequests.count { fcm ->
            (fcm.id !in acceptedIds)||(fcm.id !in declinedIds)
        }

        serverChatRequest + hasFcmChatRequests
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    init {
        fetchChatRequestsCount()
        viewModelScope.launch {
            ChatRequestEventBus.events.collect {
                addChatRequest(it)
            }
        }
    }
    private fun fetchChatRequestsCount() {
        viewModelScope.launch {
            chatUseCases.getChatRequestsCount().collect { result ->
                result.handle(
                    onLoading = {
                    },
                    onSuccess = { count ->
                        _serverChatRequestsCount.value = count
                    }
                )
            }
        }
    }

    fun addChatRequest(chatRequest: ChatRequest) {
        _fcmChatRequests.update { currentList ->
            // 중복 방지 후 최신순 추가
            if (currentList.none { it.id == chatRequest.id }) {
                listOf(chatRequest) + currentList
            } else currentList
        }
    }
}