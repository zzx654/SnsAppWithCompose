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
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.domain.model.Comment
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
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postDetailUseCases: PostDetailUseCases,
    private val commentUseCases: CommentUseCases,
    private val voteUseCases: VoteUseCases,
    locationClient: FusedLocationProviderClient,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    //로딩처리. 댓글 상단 고정
    val args:Screen.PostDetailScreen = savedStateHandle.toRoute<Screen.PostDetailScreen>()
    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()
    private val _bottomSheetDialogState =  MutableStateFlow(BottomSheetDialogState<CommentOption>())
    val bottomSheetDialogState = _bottomSheetDialogState.asStateFlow()

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



    val _isLiked = mutableStateOf(false)
    val isLiked: State<Boolean>
        get() = _isLiked
    val _isCommentsEmpty = mutableStateOf(false)
    val isCommentsEmpty: State<Boolean>
        get() = _isCommentsEmpty
    private val _getCommentsState = mutableStateOf(GetCommentsState())
    val getCommentsState: State<GetCommentsState>
        get() = _getCommentsState
    private val _notificationComment:MutableState<Comment?> =  mutableStateOf(null)
    val notificationComment:State<Comment?>
        get() = _notificationComment
    private val _notificationReply:MutableState<Comment?> =  mutableStateOf(null)
    val notificationReply:State<Comment?>
        get() = _notificationReply
    val _commentSortType = mutableStateOf(CommentSortType.OLDEST)
    val commentSortType: State<CommentSortType>
        get() = _commentSortType

    val _showContainer = mutableStateOf(false)
    val showContainer: State<Boolean>
        get() = _showContainer
    val _anonymousChecked = mutableStateOf(false)
    val anonymousChecked: State<Boolean>
        get() = _anonymousChecked
    val _post = mutableStateOf<Post?>(null)
    val post: State<Post?>
        get() = _post

    private val _mediaUiModel = MutableStateFlow(MediaUiModel(emptyList(), emptyList()))
    val mediaUiModel: StateFlow<MediaUiModel> = _mediaUiModel

    val audioUrl: String?
        get() = post.value?.media
            ?.firstOrNull { it.type == "AUDIO" }
            ?.url


    val _commentText = mutableStateOf("")
    val commentText: State<String>
        get() = _commentText
    //val _imepadding = mutableStateOf(false)
    //val imepadding: State<Boolean>
     //   get() = _imepadding

    init {
        viewModelScope.launch {
            postDetailUseCases.GetPost(
                postId = args.postId
            ).collect { result ->
                result.handle(
                    onSuccess = { postdata ->
                        if (postdata.isEmpty()) {
                            // 1. 서버에서 게시물을 찾을 수 없을 때
                            emitUiEvent(UiEvent.ShowToast(UiText.StringResource(R.string.post_not_exist_alert)))
                            emitUiEvent(UiEvent.popBackStack)
                        } else {
                            // 2. 게시물 정보 정상 로드
                            loadPostDetails(postdata[0])

                            // 3. 알림을 통해 들어온 경우 해당 댓글/답글 로드
                            args.notificationCommentId?.let { commentId ->
                                loadCommentByNotification(commentId)
                            }
                        }
                    }
                )

            }

        }

    }
    val commentPaginator =
        Paginator<Comments, Comment>(loadItems = { handleResult, refresh ->
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
                if(commentSortType.value == CommentSortType.OLDEST) {
                    commentUseCases.GetComments(post.value?.postId?:0, lastCommentId, lastCommentDate)
                        .collect {
                            handleResult(it)
                        }
                } else {
                    commentUseCases.GetPopularComments(post.value?.postId?:0, lastCommentId, lastCommentScore)
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
                comments = (
                        if (refresh) comments else getCommentsState.value.comments.filterNot{ it.commentId in newIds } + comments
                        ).filterNot {
                        it.commentId == notificationComment.value?.commentId ||
                                it.commentId == notificationReply.value?.commentId
                    },
                endReached = comments.isEmpty() && getCommentsState.value.comments.isNotEmpty()
            )

        }, extractItems = { response -> response.comments })

    private val _commentLikeStatusMap = mutableStateMapOf<Int, CommentLikeState>()
    val commentLikeStatusMap: Map<Int, CommentLikeState> get() = _commentLikeStatusMap


    /**private fun getPost(postId:Int, latitude:Double? = null,longitude:Double? = null) {
        viewModelScope.launch {
            postDetailUseCases.GetPost(postid = postId, latitude = latitude, longitude = longitude)
                .collect { result ->
                    handleResource(
                        resource = result,
                        onSuccess = { data ->
                            if(data.posts.isEmpty()) {
                                //setEvent(UiEvent.ShowToast(getString(context,R.string.post_not_exist_alert)))
                                setEvent(UiEvent.popBackStack)
                            }
                            else {

                                loadPostDetails(data.posts[0])
                                args.notificationCommentId?.let {
                                    loadCommentByNotification(it)
                                }
                            }

                        }
                    )
                }
        }
    }**/
    private fun loadCommentByNotification(commentId: Int) {
        viewModelScope.launch {
            commentUseCases.GetNotificationComment(commentId).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        result.data?.let {
                            updateStatesForNewComments(listOf(it.comment,it.reply))

                            _notificationComment.value = it.comment
                            _notificationReply.value = it.reply
                            _getCommentsState.value = getCommentsState.value.copy(
                                comments = getCommentsState.value.comments.filterNot{ comment ->
                                    comment.commentId == it.comment.commentId ||
                                            comment.commentId == it.reply?.commentId
                                } )
                        }

                    }
                    is Resource.Error -> {
                       // setEvent(
                        //    UiEvent.ShowToast(result.message ?: getString(
                            //R.string.error)))
                    }
                    else ->{}
                }


            }

        }


    }
    private fun loadPostDetails(post: Post) {
        _isLiked.value = post.isliked
        _post.value = post
        _mediaUiModel.value = post.media.toMediaUiModel()
        viewModelScope.launch {
            commentPaginator.loadNextItems(refresh = true)
        }
        viewModelScope.launch {
            voteUseCases.getVoteInfo(post.postId).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        result.data?.let {
                            if(it.voteOptions.isNotEmpty()) {
                                _voteState.value = voteState.value.copy(
                                    isMyPost = it.isMyPost,
                                    hasVoted = it.hasVoted,
                                    selectedChoiceId = it.selectedChoiceId,
                                    voteOptions = it.voteOptions,
                                    isLoading = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _voteState.value = voteState.value.copy(isLoading = false)
                       // setEvent(
                          //  UiEvent.ShowToast(result.message ?: getString(
                           // R.string.error)))
                    }
                    is Resource.Loading -> {
                        _voteState.value = voteState.value.copy(isLoading = true)

                    }

                    else -> {}
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
                            voteUseCases.vote(post.value?.postId?:0,optionId).collect { result ->
                                handleResource(
                                    resource = result,
                                    onSuccess = { data ->
                                        if(data.voteOptions.isNotEmpty()) {
                                            _voteState.value = voteState.value.copy(
                                                isMyPost = data.isMyPost,
                                                hasVoted = data.hasVoted,
                                                selectedChoiceId = data.selectedChoiceId,
                                                voteOptions = data.voteOptions
                                            )
                                        }

                                    }
                                )


                            }
                        }
                    } else {
                        voteUseCases.cancelVote(post.value?.postId?:0).collect { result ->
                            handleResource(
                                resource = result,
                                onSuccessUnit = {
                                    _voteState.value = voteState.value.copy(hasVoted = false, selectedChoiceId = null)
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

            is CommentEvent.LoadNextComments -> {
                viewModelScope.launch {
                    commentPaginator.loadNextItems(refresh = false)
                }

            }
            is CommentEvent.ToggleLikeComment -> {
                viewModelScope.launch {
                    commentUseCases.ToggleLikeComment(event.commentId).collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                val currentLikeStatus = _commentLikeStatusMap[event.commentId]?: CommentLikeState()
                                _commentLikeStatusMap[event.commentId] = CommentLikeState(
                                    isLiked = data.isLiked,
                                    likeCount = currentLikeStatus.likeCount.plus(if(data.isLiked) 1 else -1)
                                )
                            }
                        )

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
                        postId = post.value?.postId?:0,
                        text = commentText.value,
                        anonymousNick = if(anonymousChecked.value) generateAnonymousNickname() else null
                    ).collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _commentText.value = ""
                                _isCommentsEmpty.value = false
                                _getCommentsState.value = getCommentsState.value.copy(
                                    comments = listOf(data.comments[0])+getCommentsState.value.comments
                                )
                            }

                        )

                    }
                }


            }
            is CommentEvent.GotoReplyScreen -> {

                viewModelScope.launch {
                    commentUseCases.GetSelectedComment(
                        postId = post.value?.postId?:0,
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
                loadPostDetails(event.post)
            }

            is PostDetailEvent.ToggleLikePost -> {
                viewModelScope.launch {
                    postDetailUseCases.ToggleLikePost(event.postId).collect { result ->
                        handleResource(
                            resource = result,
                            onSuccess = { data ->
                                _isLiked.value = data.isLiked
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
        /**_alertDialogState.value = AlertDialogState(
            title = getString(context,R.string.delete_post_alert),
            confirmText = getString(context,R.string.confirm),
            onClickConfirm = {
                deletePost(post.value?.postId?:0)
                resetDialogState()
            },
            cancelText = getString(context,R.string.cancel),
            onClickCancel = { resetDialogState() }
        )**/
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.delete_post_alert),
            confirmText = UiText.StringResource(R.string.confirm),
            cancelText = UiText.StringResource(R.string.cancel),
            onClickConfirm = {
                deletePost(post.value?.postId ?: 0)
                resetDialogState()
            },
            onClickCancel = { resetDialogState() }
        )
    }
    private fun deletePost(postId: Int) {
        viewModelScope.launch {
            postDetailUseCases.DeletePost(postId).collect { result ->
                handleResource(
                    resource = result,
                    onSuccessUnit = {
                        setEvent(UiEvent.popBackStack)
                    }
                )

            }
        }
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogStateV2()
    }
    fun updateStatesForNewComments(newComments: List<Comment?>) {
        newComments.forEach { comment ->
            comment?.commentId?.let { commentId->
                _commentLikeStatusMap[commentId] = CommentLikeState(
                    isLiked = comment.commentLiked==1,
                    likeCount = comment.likeCount
                )
            }
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
    private fun sshowBottomSheetDialog(myUserId:Int,commentUserId:Int) {
        /**val items: MutableList<BottomSheetItem> = if(myUserId == commentUserId) {
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
        ) { resetBottomSheetDialogState() }**/
    }
    private fun resetBottomSheetDialogState() {
        _bottomSheetDialogState.value = BottomSheetDialogState()
    }

}
data class MediaUiModel(
    val visualMedia: List<Media>, // image + video
    val audioMedia: List<Media>
)
fun List<Media>.toMediaUiModel(): MediaUiModel {
    val visual = filter { it.type == MEDIA_TYPE_IMAGE || it.type == MEDIA_TYPE_VIDEO }
    val audio = filter { it.type == MEDIA_TYPE_AUDIO }
    return MediaUiModel(
        visualMedia = visual,
        audioMedia = audio
    )
}

enum class CommentSortType(@StringRes val labelResId: Int) {
    OLDEST(R.string.sort_by_date), POPULAR(R.string.sort_by_popularity)
}

sealed class KeyBoardEvent {
    object HideKeyboard : KeyBoardEvent()
    object ShowKeyboard : KeyBoardEvent()
}