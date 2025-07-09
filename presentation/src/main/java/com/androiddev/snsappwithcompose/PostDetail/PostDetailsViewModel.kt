package com.androiddev.snsappwithcompose.PostDetail

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.use_case.PostDetailUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postDetailUseCases: PostDetailUseCases,
    private val context: Context
):BaseViewModel() {
    private val _uiEvent = MutableSharedFlow<KeyBoardEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val _chatList = mutableStateListOf<String>()
    val chatList: SnapshotStateList<String>
        get() = _chatList
    val _isLoad = mutableStateOf(false)
    val isLoad: State<Boolean>
        get() = _isLoad

    val _isLiked = mutableStateOf(false)
    val isLiked: State<Boolean>
        get() = _isLiked

    val _showContainer = mutableStateOf(false)
    val showContainer: State<Boolean>
        get() = _showContainer
    val _imepadding = mutableStateOf(false)
    val imepadding: State<Boolean>
        get() = _imepadding



    fun loadPost(isLiked:Boolean) {
        _isLiked.value = isLiked

    }
    fun onEvent(event: PostDetailEvent) {
        when(event) {
            is PostDetailEvent.ToggleLikePost -> {
                viewModelScope.launch {
                    postDetailUseCases.ToggleLikePost(event.postid)
                        .collect{ result ->
                            when(result) {
                                is Resource.Success -> {
                                    result.data?.let {
                                       _isLiked.value = it.isLiked
                                    }
                                }
                                is Resource.Loading -> {

                                }
                                is Resource.Error -> {
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context, R.string.error)
                                        )
                                    )
                                }
                                else -> null
                            }

                        }
                }
                event.postid
            }
        }

    }
    fun onTextFieldFocused() {
        //이건 텍스트필드를 클릭해서 키보드가 올라오는경우(아무것도 안올라와있던상태 또는 이미 컨테이너가 올라와있는상태)
        viewModelScope.launch {
            delay(10000)
            _showContainer.value = false
            _imepadding.value = true
        }
    }
    fun showHideContainer() {
        if(_showContainer.value) {
            //이미 컨테이너가 보이는상태
            viewModelScope.launch {
                _imepadding.value = false
                _uiEvent.emit(KeyBoardEvent.ShowKeyboard)

            }
        } else {
            //컨테이너는 안보이는 상태(키보드가 올라와잇을수도)
            viewModelScope.launch {
                _imepadding.value = false
                _showContainer.value = true
                _uiEvent.emit(KeyBoardEvent.HideKeyboard)
            }


        }

    }
    fun initData(scroll:(Int)->Unit) {
        viewModelScope.launch {
            _isLoad.value = true
            delay(2000L)
            _isLoad.value = false
            _chatList.addAll(listOf("안녕하세요!", "Compose로 만든 채팅입니다.","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","33","22","11","11","11","12","13","15","15"))
            scroll(chatList.size)
        }
    }
}
sealed class KeyBoardEvent{
    object HideKeyboard:KeyBoardEvent()
    object ShowKeyboard:KeyBoardEvent()
}