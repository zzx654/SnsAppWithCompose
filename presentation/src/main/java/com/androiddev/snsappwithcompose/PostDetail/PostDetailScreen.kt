package com.androiddev.snsappwithcompose.PostDetail


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.util.KeyboardViewModel
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.getString
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import coil3.util.DebugLogger
import com.androiddev.snsappwithcompose.BaseScaffold
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.UserViewModel
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.Chips
import com.androiddev.snsappwithcompose.components.CommentInput
import com.androiddev.snsappwithcompose.components.CommentItem
import com.androiddev.snsappwithcompose.components.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.components.CustomChip
import com.androiddev.snsappwithcompose.components.LoadingDialog
import com.androiddev.snsappwithcompose.components.PollCard
import com.androiddev.snsappwithcompose.ui.theme.profileBorder
import com.androiddev.snsappwithcompose.util.MenuItem
import com.androiddev.snsappwithcompose.util.UiEvent
import kotlinx.coroutines.launch
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ContextCastToActivity")
@Composable
fun PostDetailScreen(
    post: PostPreview?,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    userViewModel: UserViewModel,
    viewModel: PostDetailsViewModel = hiltViewModel(),
    keyboardviewModel: KeyboardViewModel = hiltViewModel()
) {
    //글작성자면 투표 결과만 표시 버튼x(userid를 통해 확인)
    //다른 사용자면 투표여부메따라(userid활용 조회) 표시(했으면 라디오버튼 안했으면 투표결과와 다시투표하기버튼)
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    val dropdownMenuItem = if(post?.userId == userViewModel.userId.value) {
        listOf(
            MenuItem(getString(context,R.string.edit)){},
            MenuItem(getString(context,R.string.delete)) {}
        )

    } else {
        listOf(
            MenuItem(getString(context,R.string.block_user)){},
            MenuItem(getString(context,R.string.report)) {},
            MenuItem(getString(context,R.string.request_chat)) {}
        )
    }

    val pagerState = rememberPagerState(
        initialPage = 0
    )
    var pendingScrollByCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var imeHeigh = remember { mutableStateOf(0) }
    val ime = androidx.compose.foundation.layout.WindowInsets.ime
    val localDensity = LocalDensity.current
    LaunchedEffect(key1 = Unit) {
        val keyboardFlow = snapshotFlow {
            ime.getBottom(localDensity)
        }

        keyboardFlow.collect { keyboardHeight ->
            val diff = keyboardHeight - imeHeigh.value

            listState.scrollBy(diff.toFloat())

            imeHeigh.value = keyboardHeight
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                val totalItemsCount = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemsCount - 1&&imeHeigh.value == 0&&totalItemsCount>=10) {

                    viewModel.onCommentEvent(CommentEvent.LoadNextComments)
                }
            }
    }
    val getCommentsState = viewModel.getCommentsState.value
    LaunchedEffect(post) {

        post?.let {

            viewModel.initPost(
                isLiked = it.isliked,
                it.postId
            )
        }
    }

    LaunchedEffect(Unit) {

        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {
                    navController.navigate(event.screen)
                }
                else -> null
            }
        }
    }
    LoadingDialog {
        viewModel.isLoading.value
    }
    CustomBottomSheetDialog(
        { viewModel.customBottomSheetDialogState.value.showDialog },
        { viewModel.customBottomSheetDialogState.value.items },
        viewModel.customBottomSheetDialogState.value.onClickCancel
    )
    BaseScaffold(
        modifier = Modifier.fillMaxWidth(),
        focusManager = focusManager,
        scrollState = scrollState,
        topBar = {
            if (post != null) {
                CenterAlignedTopBar(
                    title = post.nickname,
                    onBackClick = { navController.popBackStack() },
                    actions = {
                        IconButton(onClick = { dropdownMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = dropdownMenuExpanded,
                            onDismissRequest = { dropdownMenuExpanded = false }
                        ) {
                            dropdownMenuItem.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.label) },
                                    onClick = {
                                        item.onClick() //  각 항목의 고유한 onClick 실행
                                    }
                                )
                            }
                        }
                    }
                )
            }

        },
        content = {
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {




                item {
                    if (post != null) {

                        Column {
                            post.tags?.let { tags ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Chips(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    list = tags,
                                    chip = { data: String, index: Int ->
                                        CustomChip(
                                            backgroundColor = Color.Gray,
                                            text = data,
                                        )
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(if (post.tags == null) 15.dp else 5.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ProfileImage(
                                    profileImage = post.profileImage,
                                    gender = post.gender,
                                    anonymous = post.anonymous,
                                    context = context,
                                    imageLoader = imageLoader
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        post.nickname,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${post.elapsedTime} · ${post.distance}km ",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
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
                            Text(
                                text = "이건 정말정말정말정말정말정말정말정말정말정말정말정말정말정말정말정말 긴 문장입니다. 자동으로 개행이 잘 될까요?",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            post.images?.let { images ->
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(270.dp),
                                    count = post.imageSize ?: 0
                                ) { page ->
                                    // 여기에 페이지별로 보여줄 UI 구현 (예: 이미지)
                                    // 예시:
                                    // AsyncImage(model = images[page], contentDescription = null)
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(BuildConfig.BASE_URL + images[page])
                                            .build(),
                                        imageLoader = imageLoader,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp),

                                        contentScale = ContentScale.Crop,
                                        contentDescription = null
                                    )

                                }

                                Spacer(modifier = Modifier.height(15.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    HorizontalPagerIndicator(
                                        //  modifier = Modifier.align(Alignment.TopCenter),
                                        pagerState = pagerState,
                                        //modifier = Modifier.padding(16.dp), // align 대신 padding 등으로 조절
                                        activeColor = Color.Black,
                                        inactiveColor = Color.LightGray
                                    )
                                }


                            }

                            Spacer(modifier = Modifier.height(if (post.images == null) 0.dp else 15.dp))
                            PollCard(
                              voteState = viewModel.voteState.value,
                              onOptionSelected = { optionId -> viewModel.onVoteEvent(VoteEvent.SelectOption(optionId))},
                              onVoteClick = {}
                            ) 
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
                                        imageVector = if (viewModel.isLiked.value) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                        contentDescription = null,
                                        tint = Color.DarkGray.copy(0.8f),
                                        modifier = Modifier
                                            .clickable {
                                                //viewModel.onEvent(
                                                //    PostDetailEvent.ToggleLikePost(post.postId)
                                                //)
                                                coroutineScope.launch {
                                                    listState.scrollToItem(100)  // reverseLayout=true 이므로 0번이 가장 아래임
                                                }
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = getString(context,R.string.like), color = Color.DarkGray.copy(0.8f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(vertical = 13.dp)) {
                                    androidx.compose.material3.Icon(
                                        imageVector = if (viewModel.isLiked.value) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                        contentDescription = null,
                                        tint = Color.DarkGray.copy(0.8f),
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.onPostDetailEvent(
                                                    PostDetailEvent.ToggleLikePost(post.postId)
                                                )
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = getString(context,R.string.like),color = Color.DarkGray.copy(0.8f))
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


                }
                item {
                    if(!viewModel.isCommentsEmpty.value) {
                        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp,vertical = 10.dp), contentAlignment = Alignment.TopStart) {
                            Row {
                                SelectableDotText(
                                    text = getString(context,CommentSortType.OLDEST.labelResId),
                                    selected = viewModel.commentSortType.value == CommentSortType.OLDEST,
                                    onClick = {//onEvent
                                        viewModel.onCommentEvent(CommentEvent.SetCommentSortType(CommentSortType.OLDEST))
                                    },
                                )
                                Spacer(modifier = Modifier.width(9.dp))
                                SelectableDotText(
                                    text = getString(context,CommentSortType.POPULAR.labelResId),
                                    selected = viewModel.commentSortType.value == CommentSortType.POPULAR,
                                    onClick = {//onEvent
                                        viewModel.onCommentEvent(CommentEvent.SetCommentSortType(CommentSortType.POPULAR))
                                    },
                                )
                            }
                        }


                    }
                }
                item {
                    if(getCommentsState.isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                items(
                    getCommentsState.comments
                ) { comment ->

                    //PostPrevItem(post = posts()[index],modifier = Modifier.clickable{ onPostClick(posts()[index].postId) })
                    /**Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.White)
                    )**/
                    val commentLikeStatus = viewModel.commentLikeStatusMap[comment.commentId]?:CommentLikeState(isLiked = false,likeCount = 0)
                    CommentItem(
                        comment = comment,
                        isLiked = commentLikeStatus.isLiked,
                        likeCount = commentLikeStatus.likeCount,
                        imageLoader = imageLoader,
                        onLikeClick = {
                            comment.commentId?.let {
                                viewModel.onCommentEvent(CommentEvent.ToggleLikeComment(it))
                            }
                        },
                        onOptionClick = {
                            viewModel.onCommentEvent(CommentEvent.ShowCommentOptions(
                                myUserId = userViewModel.userId.value,
                                commentUserId = comment.userId
                            ))
                        },
                        onCommentClick = {
                            viewModel.onCommentEvent(CommentEvent.GotoReplyScreen(
                                commentId = comment.commentId?:0
                            ))
                        }
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
                item {
                    if(getCommentsState.isLoading && getCommentsState.comments.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                //로딩중이 아닐때, comment가 없는상태에서 불러온결과 없을때
                item {
                    if(!getCommentsState.isLoading&&viewModel.isCommentsEmpty.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getString(context,R.string.comment_empty),
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

            }

        },
        bottomBar = {
            CommentInput(
                comment = viewModel.commentText.value,
                onCommentChange = { viewModel.onCommentEvent(CommentEvent.TypeComment(it)) },
                onPostClick = {
                    if(viewModel.commentText.value.isNotEmpty())
                        viewModel.onCommentEvent(CommentEvent.PostComment)
                },
                isAnonymous = viewModel.anonymousChecked.value,
                onAnonymousChange = { viewModel.onCommentEvent(CommentEvent.ToggleAnonymous(it)) }
            )
        },
        lazyColumnExist = true
    )


}

@Composable
fun ProfileImage(profileImage: String?, gender: String, anonymous: Boolean,context: Context,imageLoader: ImageLoader) {
    val sizeDp = 42.dp
    val sizePx = with(LocalDensity.current) { sizeDp.roundToPx() }
    if (profileImage == null || anonymous) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painterResource(id = if (gender == getString(context,R.string.male)) R.drawable.person_male else if (gender == getString(context,R.string.female)) R.drawable.person_female else R.drawable.person_none),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape) // clip to the circle shape
                .border(1.dp, profileBorder, CircleShape)
        )
    } else {
        val imageRequest = remember(profileImage) {
            ImageRequest.Builder(context)
                .data(BuildConfig.BASE_URL + profileImage)
                .size(sizePx)
                .scale(Scale.FILL)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .placeholder(R.drawable.person_none)
                .error(R.drawable.person_none)
                .build()
        }
        AsyncImage(
            model = imageRequest,
            imageLoader = imageLoader,
            modifier = Modifier
                .size(sizeDp)
                .clip(CircleShape) // clip to the circle shape
                .border(1.dp, profileBorder, CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}
@SuppressLint("RememberReturnType")
@Composable
fun SelectableDotText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = if (selected) Color.Black else Color.Gray.copy(alpha = 0.7f)
    val textColor = if (selected) Color.Black else Color.Gray.copy(alpha = 0.7f)
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            //.padding(8.dp)
    ) {
        Text(
            text = "•",
            color = dotColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
@Composable
fun ChatMessages(
    messages: List<String>,
    modifier: Modifier = Modifier,
    isLoad: Boolean,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true // 최신 메시지가 아래에 오도록 역순 배치
    ) {

        item {
            if (isLoad) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        items(messages.asReversed()) { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCF8C6), shape = MaterialTheme.shapes.medium)
                    .padding(12.dp)
            ) {
                Text(text = msg, fontSize = 16.sp)
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
fun ChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSendClick) {
            Text("전송")
        }
    }
}

@Composable
fun KeyboardHeightWithInsets(
    onKeyboardHeightChanged: (Int, Int) -> Unit
) {
    val imeInsets = androidx.compose.foundation.layout.WindowInsets.ime
    val imeBottom = imeInsets.getBottom(LocalDensity.current)
    val navHeight =
        androidx.compose.foundation.layout.WindowInsets.navigationBars.getBottom(LocalDensity.current)
    val systemBarsHeight =
        androidx.compose.foundation.layout.WindowInsets.systemBars.getBottom(LocalDensity.current)

    Log.d("Insets", "IME: $imeBottom, NavBar: $navHeight, SysBars: $systemBarsHeight")
    LaunchedEffect(imeBottom) {
        onKeyboardHeightChanged(imeBottom, navHeight)
    }
}

@Composable
fun isKeyboardVisible(): Boolean {
    val ime = androidx.compose.foundation.layout.WindowInsets.ime
    val density = LocalDensity.current
    val imeBottom = ime.getBottom(density)
    return imeBottom > 0
}

fun Modifier.conditionalImePadding(apply: Boolean): Modifier {
    return if (apply) this.imePadding() else this
}

@Composable
fun ScreenHeightPercentToPx(percent: Float): Int {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    return with(density) {
        (screenHeightDp * percent).roundToPx()
    }
}

