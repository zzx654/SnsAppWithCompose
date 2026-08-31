package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.Chips
import com.androiddev.snsappwithcompose.common.component.CustomChip
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.generateDisplayName
import com.androiddev.snsappwithcompose.feature.PostDetail.PostDetailEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioPlayer
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.component.PollCard
import kotlinx.coroutines.launch

/**@Composable
fun PostContents(

) {
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
                ProfileImage(
                    profileImage = post?.profileImage?:"",
                    gender = post?.gender?:"",
                    anonymous = post?.anonymousNickname!=null,
                    context = context,
                    imageLoader = imageLoader
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = generateDisplayName(context,post?.nickname?:"",post?.anonymousNickname),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${post?.elapsedTime} · ${post?.distance ?:0}km ",
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
                text = post?.text?:"",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )
            Spacer(modifier = Modifier.height(15.dp))

            if(mediaUiState.visualMedia.isNotEmpty()) {
                MediaGrid(
                    mediaList = mediaUiState.visualMedia,
                    onClick = { navController.navigate(Screen.MediaScreen(mediaUiState.visualMedia))}
                )

            }

            //Spacer(modifier = Modifier.height(if (post?.images == null) 0.dp else 15.dp))
            PollCard(
                voteState = voteState,
                onOptionSelected = { optionId -> postViewModel.onVoteEvent(VoteEvent.SelectOption(optionId))},
                onVoteClick = {
                    postViewModel.onVoteEvent(VoteEvent.OnVoteClick)
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
                    url = audioUrl
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
                        imageVector = if (postViewModel.isLiked.value) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
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
                    Text(text = getString(context, R.string.like), color = Color.DarkGray.copy(0.8f))
                }
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(vertical = 13.dp)) {
                    androidx.compose.material3.Icon(
                        imageVector = if (isLiked) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                        contentDescription = null,
                        tint = Color.DarkGray.copy(0.8f),
                        modifier = Modifier
                            .clickable {
                                postViewModel.onPostDetailEvent(
                                    PostDetailEvent.ToggleLikePost(
                                        post?.postId ?: 0
                                    )
                                )
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
}**/