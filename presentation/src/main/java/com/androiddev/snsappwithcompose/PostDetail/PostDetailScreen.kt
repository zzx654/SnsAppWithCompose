package com.androiddev.snsappwithcompose.PostDetail


import android.annotation.SuppressLint
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.util.DebugLogger
import com.androiddev.snsappwithcompose.BaseScaffold
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.Chips
import com.androiddev.snsappwithcompose.components.CustomChip
import com.androiddev.snsappwithcompose.ui.theme.profileBorder
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ContextCastToActivity")
@Composable
fun PostDetailScreen(
    post: PostPreview?,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    viewModel: PostDetailsViewModel = hiltViewModel(),
    keyboardviewModel: KeyboardViewModel = hiltViewModel()
) {


    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val firstVisible = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
            firstVisible != null && firstVisible == 0
        }
    }
    LaunchedEffect(Unit) {
        viewModel.initData() { size ->
            coroutineScope.launch {
                listState.scrollToItem(size)  // reverseLayout=true 이므로 0번이 가장 아래임
            }
        }

    }
    LaunchedEffect(shouldLoadMore.value) {
        val firstVisibleItemIndex = listState.firstVisibleItemIndex
        val firstVisibleItemOffset = listState.firstVisibleItemScrollOffset
        if (shouldLoadMore.value && !viewModel.isLoad.value) {


            viewModel.initData {
                coroutineScope.launch {
                    listState.scrollToItem(
                        firstVisibleItemIndex + 27,
                        firstVisibleItemOffset
                    )
                }

            }
        }
    }
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
                        IconButton(onClick = { /* TODO: 메뉴 클릭 처리 */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                    }
                )
            }

        },
        content = {

            if (post != null) {

                post.tags?.let { tags ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Chips(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
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
                    ProfileImage(post.profileImage, post.gender, post.anonymous)

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text("${post.nickname}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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


            }
        },
        bottomBar = {
            ChatInput(
                text = inputText,
                onTextChange = { inputText = it },
                onSendClick = {
                    //if (inputText.isNotBlank()) {
                    //     messages = messages + inputText
                    //    inputText = ""
                    // }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            )
        },
        lazyColumnExist = true
    )


}

@Composable
fun ProfileImage(profileImage: String?, gender: String, anonymous: Boolean) {

    if (profileImage == null || anonymous) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painterResource(id = if (gender == "남성") R.drawable.person_male else if (gender == "여성") R.drawable.person_female else R.drawable.person_none),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape) // clip to the circle shape
                .border(1.dp, profileBorder, CircleShape)
        )
    } else {
        val imageLoader = LocalContext.current.imageLoader.newBuilder()
            .logger(DebugLogger())
            .build()
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(BuildConfig.BASE_URL + profileImage)
                .build(),
            imageLoader = imageLoader,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape) // clip to the circle shape
                .border(1.dp, profileBorder, CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null
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

