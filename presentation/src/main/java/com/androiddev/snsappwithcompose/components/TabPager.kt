package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun TabPager(
    tabs: List<String>,
    pages: List<@Composable () -> Unit>,
    pagerState: PagerState = rememberPagerState { tabs.size },
) {
    val coroutineScope = rememberCoroutineScope()
    val tabIndex = pagerState.currentPage

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = tabIndex,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabIndex),
                    color = Color.Black
                )
            },
            containerColor = Color.White
        ) {
            tabs.forEachIndexed { index, value ->
                Tab(selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(text = "${tabs[index]}")
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }

        HorizontalPager(state = pagerState) { page ->

            pages[page]()
        }
    }
}