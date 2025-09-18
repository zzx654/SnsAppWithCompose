package com.androiddev.snsappwithcompose.PostDetail

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.use_case.CommentUseCases
import com.androiddev.domain.use_case.PostDetailUseCases
import com.androiddev.domain.use_case.VoteUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.Constants.PAGE_SIZE
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.home.GetPostsState
import com.androiddev.snsappwithcompose.home.nearposts.GetNearPostsEvent
import com.androiddev.snsappwithcompose.navigation.components.Screen
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.BottomSheetItem
import com.androiddev.snsappwithcompose.util.CustomBottomSheetDialogState
import com.androiddev.snsappwithcompose.util.Paginator
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.VoteState
import com.androiddev.snsappwithcompose.util.generateAnonymousNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postDetailUseCases: PostDetailUseCases,
    private val commentUseCases: CommentUseCases,
    private val voteUseCases: VoteUseCases,
    private val context: Context
) : BaseViewModel() {
    //로딩처리. 댓글 상단 고정
    private val _customBottomSheetDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val customBottomSheetDialogState: State<CustomBottomSheetDialogState>
        get() = _customBottomSheetDialogState
    private val _voteState: MutableState<VoteState> = mutableStateOf( VoteState())
    val voteState: State<VoteState>
        get() = _voteState
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
                var lastCommentScore: Int = 0
                with(getCommentsState.value.comments) {
                    if (isNotEmpty() && !refresh) {
                        lastCommentDate = last().date
                        lastCommentId = last().commentId
                        lastCommentScore = last().score
                    }
                }
                //postid를 얻을방법
                if(commentSortType.value == CommentSortType.OLDEST) {
                    commentUseCases.GetComments(postId.value, lastCommentId, lastCommentDate)
                        .collect {
                            handleResult(it)
                        }
                } else {
                    commentUseCases.GetPopularComments(postId.value, lastCommentId, lastCommentScore)
                        .collect {
                            handleResult(it)
                        }
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
            val newIds = comments.map { it.commentId }.toSet()
            updateStatesForNewComments(comments)
            _getCommentsState.value = getCommentsState.value.copy(
                comments = if (refresh) comments else getCommentsState.value.comments.filterNot{ it.commentId in newIds } + comments,
                endReached = comments.isEmpty() && getCommentsState.value.comments.isNotEmpty()
            )
        }, extractItems = { response -> response.comments })

    private val _commentLikeStatusMap = mutableStateMapOf<Int,CommentLikeState>()
    val commentLikeStatusMap: Map<Int,CommentLikeState> get() = _commentLikeStatusMap

    fun initPost(isLiked: Boolean, postId: Int) {
        _isLiked.value = isLiked
        _postId.value = postId
        viewModelScope.launch {
            commentPaginator.loadNextItems(refresh = true)
        }
        viewModelScope.launch {
            voteUseCases.getVoteInfo(postId).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        //투표 fetch
                        result.data?.let {
                            if(it.isTokenValid) {
                                if(it.voteInfo.isNotEmpty()) {
                                    _voteState.value = voteState.value.copy(
                                        isMyPost = it.isMyPost,
                                        hasVoted = it.hasVoted,
                                        selectedChoiceId = it.selectedChoiceId,
                                        voteInfo = it.voteInfo
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }

            }
        }

    }

    fun onVoteEvent(event: VoteEvent) {
        when(event) {
            is VoteEvent.OnVoteClick -> {
                viewModelScope.launch {
                    if(!voteState.value.hasVoted) {
                        voteState.value.selectedChoiceId?.let { optionId ->
                            voteUseCases.vote(postId.value,optionId).collect { result ->
                                when(result) {
                                    is Resource.Success -> {
                                        //투표 fetch
                                        setLoading(false)
                                        result.data?.let {
                                            if(it.isTokenValid) {
                                                if(it.voteInfo.isNotEmpty()) {
                                                    _voteState.value = voteState.value.copy(
                                                        isMyPost = it.isMyPost,
                                                        hasVoted = it.hasVoted,
                                                        selectedChoiceId = it.selectedChoiceId,
                                                        voteInfo = it.voteInfo
                                                    )
                                                }
                                            }
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
                    } else {
                        voteUseCases.cancelVote(postId.value).collect { result ->
                            _voteState.value = voteState.value.copy(hasVoted = false, selectedChoiceId = null)

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

            is CommentEvent.LoadNextComments -> {
                viewModelScope.launch {
                    commentPaginator.loadNextItems(refresh = false)
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
            is CommentEvent.SetCommentSortType -> {
                _commentSortType.value = event.commentSortType
                viewModelScope.launch {
                    _getCommentsState.value = GetCommentsState(comments = listOf())
                    commentPaginator.loadNextItems(refresh = true)
                }

            }

            is CommentEvent.PostComment -> {
                viewModelScope.launch {
                    commentUseCases.PostComment(
                        postId = postId.value,
                        text = commentText.value,
                        anonymousNick = if(anonymousChecked.value) generateAnonymousNickname() else null
                    ).collect { result ->
                        when(result) {

                            is Resource.Success -> {
                                setLoading(false)
                                _commentText.value = ""
                                result.data?.let {
                                    _isCommentsEmpty.value = false
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
            is CommentEvent.GotoReplyScreen -> {
                event.commentId
                postId.value
                viewModelScope.launch {
                    commentUseCases.GetSelectedComment(
                        postId = postId.value,
                        commentId = event.commentId
                    ).collect { result ->
                        when(result) {

                            is Resource.Success -> {
                                setLoading(false)
                                result.data?.let {
                                    setEvent(
                                        UiEvent.navigate(
                                            Screen.ReplyScreen(it.comments[0])
                                        )
                                    )
                                    Log.d("comment","${it.comments[0]}")
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

            else-> null
        }

    }
    fun onPostDetailEvent(event: PostDetailEvent) {
        when (event) {


            is PostDetailEvent.ToggleLikePost -> {
                viewModelScope.launch {
                    postDetailUseCases.ToggleLikePost(event.postId).collect { result ->
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
                BottomSheetItem(R.drawable.outline_edit,getString(context,R.string.edit)) {
                    resetBottomSheetDialogState()
                },
                BottomSheetItem(R.drawable.outline_delete,getString(context,R.string.delete)) {
                    resetBottomSheetDialogState()
                },
            )
        } else {
            mutableListOf(
                BottomSheetItem(R.drawable.outline_report,getString(context,R.string.report)) {
                    resetBottomSheetDialogState()
                },
                BottomSheetItem(R.drawable.outline_block,getString(context,R.string.block_user)) {
                    resetBottomSheetDialogState()
                },
                BottomSheetItem(R.drawable.outline_chat,getString(context,R.string.request_chat)) {
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

enum class CommentSortType(@StringRes val labelResId: Int) {
    OLDEST(R.string.sort_by_popularity), POPULAR(R.string.sort_by_date)
}

sealed class KeyBoardEvent {
    object HideKeyboard : KeyBoardEvent()
    object ShowKeyboard : KeyBoardEvent()
}