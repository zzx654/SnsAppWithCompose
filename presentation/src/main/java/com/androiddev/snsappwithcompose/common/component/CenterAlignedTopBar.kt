package com.androiddev.snsappwithcompose.common.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopBar(
    title: String,
    onBackClick:(()->Unit)? = null,
    leftAction: @Composable (RowScope.() -> Unit) = {},
    rightAction: @Composable (RowScope.() -> Unit) = {}
) {

    CenterAlignedTopAppBar(
        title = { Text(text = title,fontWeight = FontWeight.Bold,fontSize = 16.sp) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        navigationIcon = {
            if (onBackClick != null) {
                // 뒤로가기 아이콘
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            } else {
                // back 기능이 없을 때 leftAction 실행
                Row(content = leftAction)
            }
        },
        actions =  rightAction
    )
}