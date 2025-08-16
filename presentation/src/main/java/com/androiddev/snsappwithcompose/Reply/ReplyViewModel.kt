package com.androiddev.snsappwithcompose.Reply

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.use_case.CommentUseCases
import com.androiddev.domain.use_case.ReplyUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.PostDetail.CommentEvent
import com.androiddev.snsappwithcompose.PostDetail.CommentLikeState
import com.androiddev.snsappwithcompose.PostDetail.CommentSortType
import com.androiddev.snsappwithcompose.PostDetail.GetCommentsState
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.BottomSheetItem
import com.androiddev.snsappwithcompose.util.CustomBottomSheetDialogState
import com.androiddev.snsappwithcompose.util.Paginator
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.generateAnonymousNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val commentUseCases: CommentUseCases,
    private val replyUseCases: ReplyUseCases,
    private val context: Context
): BaseViewModel() {
    val _commentText = mutableStateOf("")
    val commentText: State<String>
        get() = _commentText
    private val _getCommentsState = mutableStateOf(GetCommentsState())
    val getCommentsState: State<GetCommentsState>
        get() = _getCommentsState
    val _anonymousChecked = mutableStateOf(false)
    val anonymousChecked: State<Boolean>
        get() = _anonymousChecked
    val _ref = mutableStateOf(0)
    val ref: State<Int>
        get() = _ref
    val _postId = mutableStateOf(0)
    val postId: State<Int>
        get() = _postId
    private val _commentLikeStatusMap = mutableStateMapOf<Int,CommentLikeState>()
    val commentLikeStatusMap: Map<Int,CommentLikeState> get() = _commentLikeStatusMap
    private val _customBottomSheetDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val customBottomSheetDialogState: State<CustomBottomSheetDialogState>
        get() = _customBottomSheetDialogState
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
                replyUseCases.GetReplies(ref.value,lastCommentId,lastCommentDate)
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

            val newIds = comments.map { it.commentId }.toSet()
            updateStatesForNewComments(comments)
            _getCommentsState.value = getCommentsState.value.copy(
                comments = if (refresh) comments else getCommentsState.value.comments.filterNot{ it.commentId in newIds } + comments,
                endReached = comments.isEmpty() && getCommentsState.value.comments.isNotEmpty()
            )
        }, extractItems = { response -> response.comments })
    fun initComment(comment:Comment){
        _ref.value = comment.ref
        _postId.value = comment.postId
        _commentLikeStatusMap[comment.commentId?:0] = CommentLikeState(
            isLiked = comment.commentLiked==1,
            likeCount = comment.likeCount
        )
        viewModelScope.launch {
            commentPaginator.loadNextItems(refresh = true)
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
            is CommentEvent.LoadNextComments -> {
                viewModelScope.launch {
                    commentPaginator.loadNextItems(refresh = false)
                }
            }
            is CommentEvent.PostReply -> {
                viewModelScope.launch {
                    replyUseCases.PostReply(
                        postId = postId.value,
                        ref = ref.value,
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
            is CommentEvent.ToggleLikeComment -> {
                viewModelScope.launch {
                    commentUseCases.ToggleLikeComment(event.commentId).collect { result ->
                        when(result) {
                            is Resource.Success -> {
                                setLoading(false)
                                result.data?.let {
                                    val currentLikeStatus = _commentLikeStatusMap[event.commentId]?: CommentLikeState()
                                    _commentLikeStatusMap[event.commentId] = CommentLikeState(
                                        isLiked = it.isLiked,
                                        likeCount = currentLikeStatus.likeCount.plus(if(it.isLiked) 1 else -1)
                                    )
                                }
                            }
                            is Resource.Loading -> Unit

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
            else -> null
        }


    }
    fun updateStatesForNewComments(newComments: List<Comment>) {
        newComments.forEach { comment ->
            comment.commentId?.let { commentId->
                _commentLikeStatusMap[commentId] = CommentLikeState(
                    isLiked = comment.commentLiked==1,
                    likeCount = comment.likeCount
                )
            }
        }
    }

    private fun showBottomSheetDialog(myUserId:Int,commentUserId:Int) {
        val items: MutableList<BottomSheetItem> = if(myUserId == commentUserId) {
            mutableListOf(
                BottomSheetItem(R.drawable.outline_edit, getString(context, R.string.edit)) {
                    resetBottomSheetDialogState()
                },
                BottomSheetItem(R.drawable.outline_delete, getString(context, R.string.delete)) {
                    resetBottomSheetDialogState()
                },
            )
        } else {
            mutableListOf(
                BottomSheetItem(R.drawable.outline_report, getString(context, R.string.report)) {
                    resetBottomSheetDialogState()
                },
                BottomSheetItem(R.drawable.outline_block, getString(context, R.string.block_user)) {
                    resetBottomSheetDialogState()
                },
                BottomSheetItem(R.drawable.outline_chat, getString(context, R.string.request_chat)) {
                    resetBottomSheetDialogState()
                }
            )
        }






        _customBottomSheetDialogState.value = CustomBottomSheetDialogState(
            showDialog = true,
            items,
        ) { resetBottomSheetDialogState() }
    }
    private fun resetBottomSheetDialogState() {
        _customBottomSheetDialogState.value = CustomBottomSheetDialogState()
    }
}