package com.androiddev.snsappwithcompose.feature.PostDetail.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.ImageLoader
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.Chips
import com.androiddev.snsappwithcompose.common.component.CustomChip
import com.androiddev.snsappwithcompose.common.component.paging.DefaultEmptyView
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.generateDisplayName
import com.androiddev.snsappwithcompose.feature.PostDetail.PostDetailEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.PostDetailUiState
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioPlayer
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioViewModel
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentItem
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.component.PollCard
import com.androiddev.snsappwithcompose.feature.Reply.ReplyItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailContent(
    navController: NavController,
    userId:Int?,
    postDetailUiState: PostDetailUiState,
    onPostDetailEvent:(PostDetailEvent)->Unit,
    onVoteEvent:(VoteEvent) -> Unit,
    notificationComment:Comment?,
    notificationReply:Comment?,
    audioViewModel: AudioViewModel,
    newlyAddedComments: List<Comment>, // StateFlow에서 collectAsStateWithLifecycle()로 받아온 값
    pagingComments: LazyPagingItems<Comment>,
    onCommentEvent:(CommentEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
    val post = postDetailUiState.post



        // 1. 게시글 본문 Header
        item {
                Column {

                    post?.tags?.let { tags ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Chips(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            list = tags.map{"#$it"},
                            chip = { data: String, index: Int ->
                                CustomChip(
                                    backgroundColor = Color.Gray,
                                    text = data,
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(if (post?.tags == null) 15.dp else 5.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        post?.gender?.let {
                            ProfileImage(
                                profileImage = post.profileImage,
                                gender = it,
                                anonymous = post.anonymousNickname!=null,
                                context = context,
                                imageLoader = imageLoader
                            )
                        }


                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            post?.nickname?.let {
                                Text(
                                    text = generateDisplayName(context,it,post.anonymousNickname),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            if(post?.elapsedTime!=null && post.distance!=null) {
                                Text(
                                    text = "${post.elapsedTime} · ${post.distance ?:0}km ",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp, // 또는 0.5.dp 등
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    post?.text?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                    val postMedia = postDetailUiState.mediaUiModel

                    if(postMedia.visualMedia.isNotEmpty()) {
                        MediaGrid(
                            mediaList = postMedia.visualMedia,
                            onClick = { navController.navigate(Screen.MediaScreen(postMedia.visualMedia))}
                        )

                    }

                    //Spacer(modifier = Modifier.height(if (post?.images == null) 0.dp else 15.dp))
                    PollCard(
                        voteState = postDetailUiState.voteState,
                        onOptionSelected = { optionId ->
                            onVoteEvent(VoteEvent.SelectOption(optionId))
                                           },
                        onVoteClick = {
                            onVoteEvent(VoteEvent.OnVoteClick)
                        }
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AudioPlayer(
                            modifier = Modifier
                                .align(Alignment.CenterEnd) // 오른쪽 끝
                                .padding(end = 8.dp,top = 10.dp),
                            viewModel = audioViewModel,
                            url = postDetailUiState.audioUrl
                        )
                    }


                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp, // 또는 0.5.dp 등
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround

                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(vertical = 13.dp)) {
                            androidx.compose.material3.Icon(
                                imageVector = if (postDetailUiState.isLiked) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                contentDescription = null,
                                tint = Color.DarkGray.copy(0.8f),
                                modifier = Modifier
                                    .clickable {

                                        //viewModel.onEvent(
                                        //    PostDetailEvent.ToggleLikePost(post.postId)
                                        //)
                                        //coroutineScope.launch {
                                         //   listState.scrollToItem(100)  // reverseLayout=true 이므로 0번이 가장 아래임
                                        //}
                                    }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = getString(context, R.string.like), color = Color.DarkGray.copy(0.8f))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(vertical = 13.dp)) {
                            androidx.compose.material3.Icon(
                                imageVector = if (postDetailUiState.isLiked) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                contentDescription = null,
                                tint = Color.DarkGray.copy(0.8f),
                                modifier = Modifier
                                    .clickable {
                                        post?.postId?.let {
                                            onPostDetailEvent(
                                                PostDetailEvent.ToggleLikePost(
                                                    it
                                                )
                                            )
                                        }

                                    }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = getString(context, R.string.like),color = Color.DarkGray.copy(0.8f))
                        }

                    }
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp, // 또는 0.5.dp 등
                        modifier = Modifier
                            .fillMaxWidth()
                    )


                }

        }
        notificationComment?.let {
            item {
                CommentRow(
                    comment = it,
                    imageLoader = imageLoader,
                    onLikeClick = {},
                    onCommentClick = {},
                    onOptionClick = {}

                )

            }
        }
        notificationReply?.let {
            item {
                ReplyRow(
                    comment = it,
                    imageLoader = imageLoader,
                    onLikeClick = {},
                    onOptionClick = {}
                )
            }
        }
        item {
            val refreshState = pagingComments.loadState.refresh

            when {
                // 1. 첫 로딩 중
                refreshState is LoadState.Loading && pagingComments.itemCount == 0 -> {
                    CircularProgressIndicator()
                }

                // 2. 데이터 로딩 완료 되었으나 개수가 0개일 때 (진짜 비어있는 상태)
                refreshState is LoadState.NotLoading
                        && pagingComments.itemCount == 0
                        && newlyAddedComments.isEmpty()
                        && notificationComment == null
                        && notificationReply == null -> {
                    DefaultEmptyView(emptyMessage = getString(context, R.string.comment_empty))
                }

                // 3. 로딩 에러 발생 시
                //refreshState is LoadState.Error -> {
                //   DefaultErrorView(...)
                //}

            }
        }


        //  방금 작성된 댓글 목록 (최신순일 때 최상단에 배치)
        items(
            items = newlyAddedComments,
            key = { it.commentId } // 고유 ID 지정
        ) { comment ->
            CommentRow(
                comment = comment,
                imageLoader = imageLoader,
                onLikeClick = {},
                onCommentClick = {},
                onOptionClick = {}

            )
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(0.2f))
        }

        // 3. 기존 Paging3 댓글 목록
        items(
            count = pagingComments.itemCount,
            key = pagingComments.itemKey { it.commentId }
        ) { index ->
            val comment = pagingComments[index]
            if (comment != null) {
                CommentRow(
                    comment = comment,
                    imageLoader = imageLoader,
                    onLikeClick = {},
                    onCommentClick = {},
                    onOptionClick = {}

                )
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(0.2f))
            }
        }
    }
}
@Composable
fun ReplyRow(
    comment: Comment,
    imageLoader: ImageLoader,
    onLikeClick:()->Unit,
    onOptionClick:() ->Unit
){
    //val commentLikeStatus = postViewModel.commentLikeStatusMap[comment.commentId]?: CommentLikeState(isLiked = false,likeCount = 0)
    ReplyItem(
        comment = comment,
        //isLiked = commentLikeStatus.isLiked,
        //likeCount = commentLikeStatus.likeCount,
        imageLoader = imageLoader,
        onLikeClick = onLikeClick,
        onOptionClick = onOptionClick
    )
    Divider(
        color = Color.LightGray,
        thickness = 1.dp
    )
}
@Composable
fun CommentRow(
    comment:Comment,
    imageLoader: ImageLoader,
    onLikeClick:()->Unit,
    onCommentClick:() ->Unit,
    onOptionClick:() ->Unit
) {
    //val commentLikeStatus = postViewModel.commentLikeStatusMap[comment.commentId]?: CommentLikeState(isLiked = false,likeCount = 0)
    CommentItem(
        comment = comment,
        //isLiked = commentLikeStatus.isLiked,
        //likeCount = commentLikeStatus.likeCount,
        imageLoader = imageLoader,
        onLikeClick = onLikeClick,
        //{
        // comment.commentId?.let {
        //    postViewModel.onCommentEvent(CommentEvent.ToggleLikeComment(it))
        //}
        //},
        onOptionClick = onOptionClick
        /**{
        postViewModel.onCommentEvent(
        CommentEvent.ShowCommentOptions(
        myUserId = userId,
        commentUserId = comment.userId
        ))
        }**/,
        onCommentClick = onCommentClick
        /**{
        comment.commentId?.let {
        postViewModel.onCommentEvent(
        CommentEvent.GotoReplyScreen(
        commentId = it
        ))

        }

        }**/
    )
    Divider(
        color = Color.LightGray,
        thickness = 1.dp
    )
}