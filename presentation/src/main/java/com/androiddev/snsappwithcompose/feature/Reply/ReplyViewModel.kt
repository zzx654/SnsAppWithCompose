package com.androiddev.snsappwithcompose.feature.Reply

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.Comments
import com.androiddev.domain.use_case.postdetail.CommentUseCases
import com.androiddev.domain.use_case.reply.ReplyUseCases
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.CommentLikeState
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.GetCommentsState
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.model.BottomSheetItem
import com.androiddev.snsappwithcompose.common.state.CustomBottomSheetDialogState
import com.androiddev.snsappwithcompose.common.util.Paginator
import com.androiddev.snsappwithcompose.common.util.generateAnonymousNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val commentUseCases: CommentUseCases,
    private val replyUseCases: ReplyUseCases,
    @ApplicationContext context: Context,
): BaseViewModel(context) {
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
    //private val _commentLikeStatusMap = mutableStateMapOf<Int, CommentLikeState>()
    //val commentLikeStatusMap: Map<Int, CommentLikeState> get() = _commentLikeStatusMap
    private val _customBottomSheetDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val customBottomSheetDialogState: State<CustomBottomSheetDialogState>
        get() = _customBottomSheetDialogState
    val commentPaginator =
        Paginator<Comments, Comment>(loadItems = { handleResult, refresh ->
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
            _getCommentsState.value = getCommentsState.value.copy(
                comments = if (refresh) comments else getCommentsState.value.comments.filterNot{ it.commentId in newIds } + comments,
                endReached = comments.isEmpty() && getCommentsState.value.comments.isNotEmpty()
            )
        }, extractItems = { response -> response.comments })
    private val _commentStateMap = MutableStateFlow<Map<Int, Comment>>(emptyMap())
    fun initComment(comment:Comment){
        _ref.value = comment.ref
        _postId.value = comment.postId
        //_commentLikeStatusMap[comment.commentId?:0] = CommentLikeState(
        //    isLiked = comment.commentLiked==1,
        //    likeCount = comment.likeCount
        //)
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
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _commentText.value = ""
                                _getCommentsState.value = getCommentsState.value.copy(comments = listOf(data.comments[0])+getCommentsState.value.comments)
                            }
                        )
                    }
                }


            }
            is CommentEvent.ToggleLikeComment -> {
                viewModelScope.launch {
                    val comment = event.comment
                    val commentId = comment.commentId
                    val currentIsLiked = comment.commentLiked == 1
                    val targetIsLiked = !currentIsLiked

                    val updatedComment = comment.toggleLike(isLiked = targetIsLiked)
                    commentUseCases.ToggleLikeComment(commentId).collect { result ->

                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _commentStateMap.update { currentMap ->
                                    currentMap + (commentId to updatedComment)
                                }
                                /**val updatedComments = getCommentsState.value.comments.map { comment ->
                                    if (comment.commentId == event.commentId) {
                                        comment.toggleLike(isLiked = data.isLiked)
                                    } else {
                                        comment
                                    }
                                }**/
                               // _getCommentsState.value = getCommentsState.value.copy(comments = updatedComments)

                            }
                        )

                    }
                }
            }
            else -> null
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