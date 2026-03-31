package com.androiddev.snsappwithcompose.feature.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.component.RadioChipButtons
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage
import com.androiddev.snsappwithcompose.feature.userprofile.component.UserProfileHeader

@Composable
fun UserProfileScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    userProfileViewModel:UserProfileViewModel
) {
    val context = LocalContext.current
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    val videoList = remember {
        List(21) { "Video $it" } // 일부러 홀수
    }
    val selectedTab = userProfileViewModel.selectedTab.value
    val focusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                CenterAlignedTopBar(
                    title = "",
                    onBackClick = { navController.popBackStack() },
                )
            }
        }

    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(horizontal = 19.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // 1. 헤더
            item {
                Spacer(modifier = Modifier.height(20.dp))
                UserProfileHeader(
                    context = context,
                    imageLoader = imageLoader


                )
               // ChannelHeader()
            }

            // 2. 구독 버튼
            item {
                Spacer(modifier = Modifier.height(20.dp))
                ActionSection()
                Spacer(modifier = Modifier.height(10.dp))
            }
            stickyHeader {
                RadioChipButtons(
                    items = userProfileViewModel.tabs,
                    selectedValue = selectedTab,
                    onSelect = { userProfileViewModel.selectTab(it) },
                    label = {
                        when (it) {
                            UserContent.HOME -> getString(context, R.string.home)
                            UserContent.PHOTO -> getString(context,R.string.photo)
                            UserContent.VIDEO -> getString(context,R.string.video)
                        }
                    }

                )
                //CustomTabSection(
                 //   selectedTab = selectedTab,
                  //  onTabSelected = { selectedTab = it }
               // )
            }

            // 3. 탭 (고정)
            /**stickyHeader {
                TabRow(selectedTabIndex = selectedTab) {

                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("List") }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Grid") }
                    )
                }
            }**/

            when (selectedTab) {
                UserContent.HOME -> {
                    items(videoList) { item ->
                        VideoListItem(item)
                    }

                }
                UserContent.PHOTO-> {
                    items(videoList.chunked(2)) { row ->
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)


                        ) {
                            row.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    VideoGridItem(item)
                                }

                            }

                            if(row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))

                            }

                        }
                    }

                }
                UserContent.VIDEO -> {

                }
            }


        }


    }




}
@Composable
fun CustomChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color.Black else Color.LightGray
    val textColor = if (selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .height(36.dp)
            .background(
                color = backgroundColor,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
fun CustomTabSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        CustomChip(
            text = "List",
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        CustomChip(
            text = "Grid",
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
    }
}
@Composable
fun VideoListItem(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
           // .padding(16.dp)
    )
}

@Composable
fun VideoGridItem(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(title)
    }
}
@Composable
fun ChannelHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text("HEADER", color = Color.White)
    }
}
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isPrimary) Color.Black else Color.LightGray
    val contentColor = if (isPrimary) Color.White else Color.Black

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text)
        }


    }

}
@Composable
fun ActionSection() {
    var isFollowing by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ActionButton(
            text = if (isFollowing) "Following" else "Follow",
            icon = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
            isPrimary = !isFollowing,
            onClick = { isFollowing = !isFollowing },
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            text = "Message",
            icon = Icons.Default.Email,
            isPrimary = false,
            onClick = { /* 채팅 */ },
            modifier = Modifier.weight(1f)
        )
    }
}