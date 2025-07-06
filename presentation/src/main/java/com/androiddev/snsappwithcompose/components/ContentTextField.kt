package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.AppTextStyles
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.ui.theme.TextFieldBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ContentTextField(
    state: TextFieldState,
    scrollState: ScrollState,
    hint: String = "",
) {
    val coroutineScope = rememberCoroutineScope()
    var lastCursor by remember { mutableStateOf(-1) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 360.dp)
            .background(
                color = TextFieldBackground.copy(alpha = 0.1f),
                shape = RoundedCornerShape(size = 16.dp)
            )
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            state = state,
            onTextLayout = {
                val layoutResult = it()
                if (layoutResult != null) {
                    coroutineScope.launch {
                        delay(100)
                        val cursorPos =
                            state.selection.start.coerceAtMost(layoutResult.layoutInput.text.length)
                        // 스크롤 튀는 현상 방지: 1. 이전 위치와 다르고, 2. 유효할 때만
                        if (cursorPos != lastCursor && cursorPos in 1..<layoutResult.layoutInput.text.length) {
                            lastCursor = cursorPos
                            val cursorRect = layoutResult.getCursorRect(cursorPos)
                            scrollState.animateScrollTo(
                                cursorRect.top.roundToInt().coerceAtLeast(0)
                            )
                        }
                    }
                }
            },
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier.padding(all = 20.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = hint,
                            style = AppTextStyles.textStyle.copy(color = Color.Gray)
                        )
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            textStyle = AppTextStyles.textStyle,
        )
    }

}