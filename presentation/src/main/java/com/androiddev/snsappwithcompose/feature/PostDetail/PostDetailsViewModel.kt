package com.androiddev.snsappwithcompose.feature.PostDetail

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.CommentSortType
import com.androiddev.domain.model.Comments
import com.androiddev.domain.model.Media
import com.androiddev.domain.model.Post
import com.androiddev.domain.use_case.postdetail.CommentUseCases
import com.androiddev.domain.use_case.postdetail.PostDetailUseCases
import com.androiddev.domain.use_case.postdetail.VoteUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.CommentLikeState
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.GetCommentsState
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteState
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.Paginator
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.state.BottomSheetDialogState

import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.common.util.generateAnonymousNickname
import com.androiddev.snsappwithcompose.common.util.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postDetailUseCases: PostDetailUseCases,
    private val commentUseCases: CommentUseCases,
    private val voteUseCases: VoteUseCases,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    //로딩처리. 댓글 상단 고정
    val args:Screen.PostDetailScreen = savedStateHandle.toRoute<Screen.PostDetailScreen>()
    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()
    private val _bottomSheetDialogState =  MutableStateFlow(BottomSheetDialogState<CommentOption>())
    val bottomSheetDialogState = _bottomSheetDialogState.asStateFlow()

    private val _postDetailUiState = MutableStateFlow(PostDetailUiState(isLoading = true))
    val postDetailUiState: StateFlow<PostDetailUiState> = _postDetailUiState.asStateFlow()
    private val _voteState = MutableStateFlow(VoteState())
    val voteState: StateFlow<VoteState> = _voteState.asStateFlow()
    private val _commentStateMap = MutableStateFlow<Map<Int, Comment>>(emptyMap())
    val commentStateMap: StateFlow<Map<Int,Comment>> = _commentStateMap
    private val _isCommentsEmpty = mutableStateOf(false)
    val isCommentsEmpty: State<Boolean>
        get() = _isCommentsEmpty

    private val _newlyAddedComments = MutableStateFlow<List<Comment>>(emptyList())
    val newlyAddedComments: StateFlow<List<Comment>> = _newlyAddedComments.asStateFlow()

    private val _deletedComments = MutableStateFlow<List<Comment>>(emptyList())
    val deletedComments: StateFlow<List<Comment>> = _deletedComments.asStateFlow()
    //private val _getCommentsState = mutableStateOf(GetCommentsState())
    //val getCommentsState: State<GetCommentsState>
     //   get() = _getCommentsState
    private val _notificationComment:MutableStateFlow<Comment?> =  MutableStateFlow(null)
    val notificationComment:StateFlow<Comment?> = _notificationComment
    private val _notificationReply:MutableStateFlow<Comment?> =  MutableStateFlow(null)
    val notificationReply:StateFlow<Comment?> = _notificationReply
    private val _commentSortType = MutableStateFlow(CommentSortType.OLDEST)
    val commentSortType: StateFlow<CommentSortType> = _commentSortType.asStateFlow()

    private val _anonymousChecked = MutableStateFlow(false)
    val anonymousChecked: StateFlow<Boolean> = _anonymousChecked





    val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    init {
        fetchPostDetail()
        args.notificationCommentId?.let { commentId ->
            loadCommentByNotification(commentId)
        }
    }
    val pagingCommentStream: Flow<PagingData<Comment>> = combine(
        _commentSortType,
        _notificationComment,
        _notificationReply,
        _newlyAddedComments
    ) { sort, notiComment, notiReply, newlyAdded ->
        val excludeIds = buildSet {
            notiComment?.commentId?.let { add(it) }
            notiReply?.commentId?.let { add(it) }
            addAll(newlyAdded.mapNotNull { it.commentId })
        }
        Pair(sort, excludeIds)
    }.flatMapLatest { (sort, excludeIds) ->

        commentUseCases.GetComments(postId = args.postId, sortType = sort)
            .map { pagingData ->
                pagingData.filter { comment -> comment.commentId !in excludeIds }
            }
    }.cachedIn(viewModelScope)

    private fun fetchPostDetail() {
        viewModelScope.launch {
            postDetailUseCases.GetPost(postId = args.postId).collect { result ->
                result.handle (
                    onLoading = {
                        _postDetailUiState.update { it.copy(isLoading = true) }
                    },
                    onSuccess = { post ->
                        if (post.isEmpty()) {
                            emitUiEvent(UiEvent.ShowToast(UiText.StringResource(R.string.post_not_exist_alert)))
                            emitUiEvent(UiEvent.popBackStack)
                        } else {
                            val fetchedPost = post[0]
                            val hasNoVote = fetchedPost.vote == null

                            _postDetailUiState.update { currentState ->
                                currentState.copy(
                                    post = fetchedPost,
                                    isLiked = fetchedPost.isliked,
                                    voteState = if (hasNoVote) {
                                        currentState.voteState.copy(isLoading = false)
                                    } else {
                                        currentState.voteState
                                    }
                                )
                            }
                            if (!hasNoVote) {
                                fetchVoteInfo()
                            }
                        }
                    },
                    onFinally = {
                        _postDetailUiState.update{ it.copy(isLoading = false)}
                    }
                )
            }
        }
    }
    private fun fetchVoteInfo() {
        viewModelScope.launch {
            voteUseCases.getVoteInfo(args.postId).collect { result ->
                result.handle(
                    onSuccess = { vote ->
                        if(vote.voteOptions.isNotEmpty()) {
                            _postDetailUiState.update {
                                it.copy(
                                    voteState = it.voteState.copy(
                                        isMyPost = vote.isMyPost,
                                        hasVoted = vote.hasVoted,
                                        selectedChoiceId = vote.selectedChoiceId,
                                        voteOptions = vote.voteOptions,
                                        isLoading = false
                                    )
                                )
                            }

                        }
                    },
                    onLoading = {
                        _postDetailUiState.update {
                            it.copy( voteState = it.voteState.copy(isLoading = true))
                        }
                    },
                    onFinally = {
                        _postDetailUiState.update {
                            it.copy(
                                voteState = it.voteState.copy(isLoading = false)
                            )
                        }
                    }
                )

            }

        }


    }
    private fun loadCommentByNotification(commentId: Int) {
        viewModelScope.launch {
            commentUseCases.GetNotificationComment(commentId).collect { result ->
                result.handle (
                    onSuccess = { notiComment ->
                        _notificationComment.value = notiComment.comment
                        _notificationReply.value = notiComment.reply
                    },
                    onLoading = {}
                )
            }
        }
    }
    fun onVoteEvent(event: VoteEvent) {
        when(event) {
            is VoteEvent.OnVoteClick -> {
                viewModelScope.launch {
                    if(!voteState.value.hasVoted) {
                        voteState.value.selectedChoiceId?.let { optionId ->
                            voteUseCases.vote(args.postId,optionId).collect { result ->
                                result.handle(
                                    onSuccess = { vote ->
                                        if(vote.voteOptions.isNotEmpty()) {
                                            _postDetailUiState.update { currentUiState ->
                                                currentUiState.copy(
                                                    voteState = currentUiState.voteState.copy(
                                                        isMyPost = vote.isMyPost,
                                                        hasVoted = vote.hasVoted,
                                                        selectedChoiceId = vote.selectedChoiceId,
                                                        voteOptions = vote.voteOptions
                                                    )
                                                )

                                            }
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        voteUseCases.cancelVote(args.postId).collect { result ->
                            result.handle(
                                onSuccess = {
                                    _postDetailUiState.update{
                                        it.copy(
                                            voteState = it.voteState.copy(
                                                hasVoted = false,
                                                selectedChoiceId = null
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            is VoteEvent.SelectOption -> {
                _voteState.value = voteState.value.copy(
                    selectedChoiceId = event.optionId
                )
            }
        }

    }
    fun onCommentEvent(event: CommentEvent) {
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
            is CommentEvent.SetCommentSortType -> {
                _commentSortType.value = event.commentSortType
            }

            is CommentEvent.PostComment -> {
                viewModelScope.launch {
                    commentUseCases.PostComment(
                        postId = args.postId?:0,
                        text = commentText.value,
                        anonymousNick = if(anonymousChecked.value) generateAnonymousNickname() else null
                    ).collect { result ->
                        result.handle(
                            onSuccess = {
                                _commentText.value = ""
                                _isCommentsEmpty.value = false
                                _newlyAddedComments.update { currentList ->
                                    currentList + it.comments
                                }
                            }
                        )
                    }
                }
            }
            is CommentEvent.GotoReplyScreen -> {
                /**id만 보내도록 변경하기**/
                viewModelScope.launch {
                    commentUseCases.GetSelectedComment(
                        postId = args.postId?:0,
                        commentId = event.commentId
                    ).collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                setEvent(
                                    UiEvent.navigate(
                                        Screen.ReplyScreen(data.comments[0])
                                    )
                                )
                                Log.d("comment","${data.comments[0]}")

                            }
                        )
                    }
                }
            }
            else-> null
        }
    }
    fun onPostDetailEvent(event: PostDetailEvent) {
        when (event) {
            is PostDetailEvent.LoadEditedPostDetails -> {
                _postDetailUiState.update { currentState ->
                    currentState.copy(
                        post = event.post
                    )
                }
                fetchVoteInfo()
            }

            is PostDetailEvent.ToggleLikePost -> {
                viewModelScope.launch {
                    postDetailUseCases.ToggleLikePost(event.postId).collect { result ->
                        result.handle(
                            onSuccess = {
                                _postDetailUiState.update { currentUiState ->
                                    currentUiState.copy(isLiked = it.isLiked) }
                            }
                        )
                    }
                }
            }
            is PostDetailEvent.DeletePost -> {
              showDeleteAlert()
            }
        }
    }
    private fun showDeleteAlert() {

        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.delete_post_alert),
            confirmText = UiText.StringResource(R.string.confirm),
            cancelText = UiText.StringResource(R.string.cancel),
            onClickConfirm = {
                deletePost(args.postId ?: 0)
                resetDialogState()
            },
            onClickCancel = { resetDialogState() }
        )
    }
    private fun deletePost(postId: Int) {
        viewModelScope.launch {
            postDetailUseCases.DeletePost(postId).collect { result ->
                result.handle(
                    onSuccess = {
                        setEvent(UiEvent.popBackStack)
                    }
                )
            }
        }
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogStateV2()
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
data class MediaUiModel(
    val visualMedia: List<Media>, // image + video
    val audioMedia: List<Media>
)


sealed class KeyBoardEvent {
    object HideKeyboard : KeyBoardEvent()
    object ShowKeyboard : KeyBoardEvent()
}