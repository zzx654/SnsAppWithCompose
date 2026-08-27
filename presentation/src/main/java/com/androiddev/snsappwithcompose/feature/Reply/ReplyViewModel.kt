package com.androiddev.snsappwithcompose.feature.Reply

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.Comment
import com.androiddev.domain.use_case.postdetail.CommentUseCases
import com.androiddev.domain.use_case.reply.ReplyUseCases
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.state.BottomSheetDialogState
import com.androiddev.snsappwithcompose.common.util.generateAnonymousNickname
import com.androiddev.snsappwithcompose.feature.PostDetail.CommentOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val commentUseCases: CommentUseCases,
    private val replyUseCases: ReplyUseCases,
    savedStateHandle: SavedStateHandle
): BaseViewModel() {

    val args: Screen.ReplyScreen = savedStateHandle.toRoute<Screen.ReplyScreen>()
    private val _originalCommentUiState: MutableStateFlow<OrigianlCommentUiState> = MutableStateFlow(OrigianlCommentUiState())
    val originalCommentUiState:StateFlow<OrigianlCommentUiState> = _originalCommentUiState.asStateFlow()
    private val _commentText: MutableState<String> = mutableStateOf("")
    val commentText: State<String> = _commentText

    private val _anonymousChecked = MutableStateFlow(false)
    val anonymousChecked: StateFlow<Boolean> = _anonymousChecked.asStateFlow()

    private val _newlyAddedComments = MutableStateFlow<List<Comment>>(emptyList())
    val newlyAddedComments: StateFlow<List<Comment>> = _newlyAddedComments.asStateFlow()
    val pagingDataStream: Flow<PagingData<Comment>> = originalCommentUiState
        .map { it.comment }
        .filterNotNull()
        .distinctUntilChangedBy{ it.commentId }
        .flatMapLatest { comment ->
            replyUseCases.GetReplies(comment.ref)
        }
        .cachedIn(viewModelScope)



    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()
    private val _bottomSheetDialogState =  MutableStateFlow(BottomSheetDialogState<CommentOption>())
    val bottomSheetDialogState = _bottomSheetDialogState.asStateFlow()
    private val _commentStateMap = MutableStateFlow<Map<Int, Comment>>(emptyMap())
    val commentStateMap: StateFlow<Map<Int,Comment>> = _commentStateMap.asStateFlow()

    init {
        fetchComment()
    }

    fun fetchComment() {
        viewModelScope.launch {
            replyUseCases.GetSelectedComment(
                commentId = args.commentId
            ).collect { result ->
                result.handle(
                    onLoading = {
                        _originalCommentUiState.update {
                            it.copy(isLoading = true)
                        }
                    },
                    onSuccess = {
                        _originalCommentUiState.update {
                            it.copy(comment = result.data?.firstOrNull())
                        }
                    },
                    onFinally = {
                        _originalCommentUiState.update {
                            it.copy(isLoading = false)
                        }
                    }
                )
            }
        }

    }

    fun onEvent(event: CommentEvent) {
        when(event) {
            is CommentEvent.ShowCommentOptions -> {
                showBottomSheetDialog(
                    myUserId = event.myUserId,
                    commentUserId = event.commentUserId
                )
            }
            is CommentEvent.TypeComment -> {
                _commentText.value = event.comment
            }

            is CommentEvent.ToggleAnonymous -> {
                _anonymousChecked.value = event.checked
            }
            is CommentEvent.PostReply -> {
                viewModelScope.launch {
                    originalCommentUiState.value.comment?.let {
                        replyUseCases.PostReply(
                            postId = it.postId,
                            ref = it.ref,
                            text = commentText.value,
                            anonymousNick = if(anonymousChecked.value) generateAnonymousNickname() else null
                        ).collect { result ->
                            result.handle(
                                onSuccess = {
                                    _commentText.value = ""
                                    _newlyAddedComments.update { currentList ->
                                        currentList + it
                                    }
                                }
                            )
                        }
                    }
                }
            }
            is CommentEvent.ToggleLikeComment -> {
                val comment = event.comment
                val commentId = comment.commentId ?: return
                val currentIsLiked = comment.commentLiked == 1
                val targetIsLiked = !currentIsLiked

                val updatedComment = comment.toggleLike(isLiked = targetIsLiked)


                viewModelScope.launch {
                    commentUseCases.ToggleLikeComment(commentId).collect { result ->
                        result.handle(
                            onSuccess = {
                                _commentStateMap.update { currentMap ->
                                    currentMap + (commentId to updatedComment)
                                }
                            },
                            onError = {
                                _commentStateMap.update { currentMap ->
                                    currentMap + (commentId to comment) // 원래 상태로 원복
                                }
                            }
                        )
                    }
                }
            }
            else -> null
        }


    }

    private fun showBottomSheetDialog(myUserId: Int, commentUserId: Int) {
        val options = if (myUserId == commentUserId) {
            listOf(CommentOption.Edit, CommentOption.Delete)
        } else {
            listOf(CommentOption.Report, CommentOption.Block, CommentOption.RequestChat)
        }

        _bottomSheetDialogState.value = BottomSheetDialogState(
            showDialog = true,
            options = options,
            onOptionSelected = { option ->
                resetBottomSheetDialogState()
                handleCommentOption(option)
            },
            onClickCancel = { resetBottomSheetDialogState() }
        )
    }
    private fun handleCommentOption(option: CommentOption) {
        when (option) {
            CommentOption.Edit -> { resetBottomSheetDialogState() }
            CommentOption.Delete -> { resetBottomSheetDialogState() }
            CommentOption.Report -> { resetBottomSheetDialogState() }
            CommentOption.Block -> { resetBottomSheetDialogState() }
            CommentOption.RequestChat -> { resetBottomSheetDialogState() }
        }
    }

    private fun resetBottomSheetDialogState() {
        _bottomSheetDialogState.value = BottomSheetDialogState()
    }


}