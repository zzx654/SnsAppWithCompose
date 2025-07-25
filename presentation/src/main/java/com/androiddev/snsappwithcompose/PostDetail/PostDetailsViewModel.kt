package com.androiddev.snsappwithcompose.PostDetail

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.use_case.PostDetailUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.Constants.PAGE_SIZE
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.home.GetPostsState
import com.androiddev.snsappwithcompose.home.nearposts.GetNearPostsEvent
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.Paginator
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.generateAnonymousNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postDetailUseCases: PostDetailUseCases, private val context: Context
) : BaseViewModel() {
    //로딩처리. 댓글 상단 고정
    private val _uiEvent = MutableSharedFlow<KeyBoardEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val _chatList = mutableStateListOf<String>()
    val chatList: SnapshotStateList<String>
        get() = _chatList
    val _isLoad = mutableStateOf(false)
    val isLoad: State<Boolean>
        get() = _isLoad
    val _postId = mutableStateOf(0)
    val postId: State<Int>
        get() = _postId



    val _isLiked = mutableStateOf(false)
    val isLiked: State<Boolean>
        get() = _isLiked
    val _isCommentsEmpty = mutableStateOf(false)
    val isCommentsEmpty: State<Boolean>
        get() = _isCommentsEmpty
    private val _getCommentsState = mutableStateOf(GetCommentsState())
    val getCommentsState: State<GetCommentsState>
        get() = _getCommentsState
    val _commentSortType = mutableStateOf(CommentSortType.OLDEST)
    val commentSortType: State<CommentSortType>
        get() = _commentSortType

    val _showContainer = mutableStateOf(false)
    val showContainer: State<Boolean>
        get() = _showContainer
    val _anonymousChecked = mutableStateOf(false)
    val anonymousChecked: State<Boolean>
        get() = _anonymousChecked
    val _commentText = mutableStateOf("")
    val commentText: State<String>
        get() = _commentText
    val _imepadding = mutableStateOf(false)
    val imepadding: State<Boolean>
        get() = _imepadding

    val commentPaginator =
        Paginator<GetCommentsResponse, Comment>(loadItems = { handleResult, refresh ->
            viewModelScope.launch {
                //등록순인지 인기순인지에 따라 요청하면됨
                var lastCommentId: Int? = null
                var lastCommentDate: String? = null
                with(getCommentsState.value.comments) {
                    if (isNotEmpty() && !refresh) {
                        lastCommentDate = last().date
                        lastCommentId = last().commentId
                    }
                }
                //postid를 얻을방법
                postDetailUseCases.GetComments(postId.value, lastCommentId, lastCommentDate)
                    .collect {
                        handleResult(it)
                    }
            }
        }, onRefreshUpdated = { isRefreshing ->
            _getCommentsState.value =
                _getCommentsState.value.copy(isRefreshing = isRefreshing, endReached = false)
        }, onLoadUpdated = { isLoading ->
            _getCommentsState.value = _getCommentsState.value.copy(isLoading = isLoading)
        }, onError = { message ->
            _getCommentsState.value = getCommentsState.value.copy(error = message)
        }, onSuccess = { comments, refresh ->
            _isCommentsEmpty.value =
                _getCommentsState.value.comments.isEmpty() && comments.isEmpty()
            _getCommentsState.value = getCommentsState.value.copy(
                comments = if (refresh) comments else getCommentsState.value.comments + comments,
                endReached = comments.isEmpty() && getCommentsState.value.comments.isNotEmpty()
            )
        }, extractItems = { response -> response.comments })

    fun initPost(isLiked: Boolean, postId: Int) {
        _isLiked.value = isLiked
        _postId.value = postId
        viewModelScope.launch {
            commentPaginator.loadNextItems(refresh = true)
        }
    }

    fun initData(){
        viewModelScope.launch {
            _isLoad.value = true
            delay(1000)

            _chatList.addAll(listOf("1","2","3","4")
                //"7","8","9","10")

            )
            _isLoad.value = false
        }

    }
    fun addChat(message:String) {
        _chatList.add(message)
    }
    fun onEvent(event: PostDetailEvent) {
        when (event) {
            is PostDetailEvent.TypeComment -> {
                _commentText.value = event.comment
            }

            is PostDetailEvent.ToggleAnonymous -> {
                _anonymousChecked.value = event.checked
            }

            is PostDetailEvent.LoadNextComments -> {
                viewModelScope.launch {
                    commentPaginator.loadNextItems(refresh = false)
                }
            }

            is PostDetailEvent.ToggleLikePost -> {
                viewModelScope.launch {
                    postDetailUseCases.ToggleLikePost(event.postid).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    setLoading(false)
                                    result.data?.let {
                                        _isLiked.value = it.isLiked
                                    }
                                }

                                is Resource.Loading -> {
                                    setLoading(true)
                                }

                                is Resource.Error -> {
                                    setLoading(false)
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(
                                                context, R.string.error
                                            )
                                        )
                                    )
                                }

                                else -> null
                            }

                        }
                }
            }

            is PostDetailEvent.PostComment -> {
                viewModelScope.launch {
                    postDetailUseCases.PostComment(
                        postId = postId.value,
                        text = commentText.value,
                        anonymousNick = if(anonymousChecked.value) generateAnonymousNickname() else null
                    ).collect { result ->
                        when(result) {

                            is Resource.Success -> {
                                setLoading(false)
                                _commentText.value = ""
                                result.data?.let {
                                    _getCommentsState.value = getCommentsState.value.copy(
                                        comments = listOf(it.comments[0])+getCommentsState.value.comments
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                setLoading(true)

                            }

                            is Resource.Error -> {
                                setLoading(false)
                                setEvent(
                                    UiEvent.ShowToast(
                                        message = result.message ?: getString(
                                            context, R.string.error
                                        )
                                    )
                                )
                            }
                        }

                    }
                }


            }
        }
    }

}


enum class CommentSortType {
    OLDEST, POPULAR
}

sealed class KeyBoardEvent {
    object HideKeyboard : KeyBoardEvent()
    object ShowKeyboard : KeyBoardEvent()
}