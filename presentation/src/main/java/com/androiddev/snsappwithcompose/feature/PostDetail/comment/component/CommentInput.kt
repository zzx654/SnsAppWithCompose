package com.androiddev.snsappwithcompose.feature.PostDetail.comment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.ui.theme.BottomBar
import com.androiddev.snsappwithcompose.ui.theme.TextFieldBackground
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentInput(
    comment: String,
    onCommentChange: (String) -> Unit,
    onPostClick: () -> Unit,
    isAnonymous: Boolean,
    onAnonymousChange: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Column(

    ) {
        HorizontalDivider(color = Color.Gray, thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BottomBar.copy(alpha = 0.5f)).padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 익명 체크박스
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = isAnonymous,
                        onCheckedChange = { onAnonymousChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Black,
                        )
                    )
                }
                Text(text = "익명",fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
            }

            // 댓글 입력 필드
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp, max = 200.dp)
                    .background(TextFieldBackground.copy(alpha = 0.1f))
                    .border(0.2.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                BasicTextField(
                    value = comment,
                    onValueChange = {
                        onCommentChange(it)

                        coroutineScope.launch {
                            // 커서 위치로 스크롤 이동
                            textLayoutResult?.let { layout ->
                                val lastLine = layout.lineCount - 1
                                val lastLineBottom = layout.getLineBottom(lastLine)
                                scrollState.animateScrollTo(lastLineBottom.toInt())
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(8.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    cursorBrush = SolidColor(Color.Black),
                    maxLines = Int.MAX_VALUE,
                    onTextLayout = { layoutResult ->
                        textLayoutResult = layoutResult
                    },
                    decorationBox = { innerTextField ->
                        if (comment.isEmpty()) {
                            Text(getString(LocalContext.current, R.string.input_comment), color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }

            // 댓글 게시 버튼
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(
                    onClick = onPostClick,
                    modifier = Modifier.padding(start = 10.dp,end = 13.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "게시",
                        tint = if (comment.isEmpty()) Color.LightGray else Color.Black
                    )
                }
            }
        }
    }

}