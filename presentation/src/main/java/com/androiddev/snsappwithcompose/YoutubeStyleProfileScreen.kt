package com.androiddev.snsappwithcompose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YouTubeStyleProfileScreen() {
    BoxWithConstraints {
        val screenHeight = maxHeight
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            // [영역 1] 프로필 헤더 영역
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "HEADER")
            }

            // [영역 2 & 3] 고정 크기(screenHeight)를 가져서 탭바가 상단에 붙게 만드는 컨테이너
            Column(modifier = Modifier.height(screenHeight)) {
                val tabList = listOf("Tab1", "Tab2 (Grid)", "Tab3")
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { tabList.size }
                )
                val coroutineScope = rememberCoroutineScope()

                // 탭바 영역
                TabRow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        if (pagerState.currentPage < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            )
                        }
                    }
                ) {
                    tabList.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                }

                // [치트키 구현부] 찾아내신 마스터 터치 가로채기 커넥션
                val pagerNestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            //아래로스크롤할때는 내버려둠?
                            // 위로 스크롤할 때(available.y < 0)만 가로채서 부모 scrollState를 억지로 전진시킵니다.
                            return if (available.y > 0) Offset.Zero else Offset(
                                x = 0f,
                                y = -scrollState.dispatchRawDelta(-available.y)
                            )
                        }

                    }
                }

                // 탭 콘텐츠 영역 (HorizontalPager)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .nestedScroll(pagerNestedScrollConnection) // 마스터 커넥션 장착
                ) { page: Int ->
                    when (page) {
                        0 -> ListLazyColumn(1)  // 💥 테스트를 위해 일부러 아이템 1개만 배치! (완벽 구동 확인용)
                        1 -> GridLazyColumn(15) // ⭐ 요청하셨던 2번 탭 그리드 완벽 연동
                        2 -> ListLazyColumn(3)  // 3번 탭 리스트 (아이템 3개)
                    }
                }
            }
        }
    }
}

// 일반 리스트 컴포저블
@Composable
fun ListLazyColumn(items: Int) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { index ->
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "Button $index")
            }
        }
    }
}

// ⭐ 새로 추가된 그리드(Grid) 컴포저블 - 마스터 스크롤과 완전하게 호환됩니다.
@Composable
fun GridLazyColumn(items: Int) {
    val gridItems = List(items) { "Grid Item $it" }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(gridItems) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(4.dp)
                    .background(Color(0xFFF5F5F5)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(text = item, fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}