package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Dialog
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.AlertDialogState

@Composable
fun AlertDialog(
    title: () -> String,
    cancelText:() ->String,
    confirmText:()->String,
    onClickCancel: ()->Unit,
    onClickConfirm: () -> Unit
) {
    val a = onClickCancel()
    if(title().isNotBlank()) {
        Dialog(
            onDismissRequest = { onClickCancel() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Card(
                shape = RoundedCornerShape(8.dp)
            )
            {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .wrapContentHeight()
                        .background(
                            color = Color.White,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = title(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color.LightGray)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Row의 높이를 내부 컴포넌트에 맞춤
                    ) {
                        if(cancelText().isNotBlank()) {
                            Button(
                                onClick = { onClickCancel() },
                                shape = RectangleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White, // 버튼 배경색상
                                    contentColor = Color.Black, // 버튼 텍스트 색상
                                    disabledContainerColor = Color.Gray, // 버튼 비활성화 배경 색상
                                    disabledContentColor = Color.White, // 버튼 비활성화 텍스트 색상
                                ),

                                ) {
                                Text(
                                    text = cancelText(),
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                                    .background(color = Color.LightGray)
                            )
                        }


                        Button(
                            onClick = { onClickConfirm() },
                            shape = RectangleShape,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White, // 버튼 배경색상
                                contentColor = Color.Red, // 버튼 텍스트 색상
                                disabledContainerColor = Color.Gray, // 버튼 비활성화 배경 색상
                                disabledContentColor = Color.White, // 버튼 비활성화 텍스트 색상
                            ),
                        ) {
                            Text(
                                text = confirmText(),
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }

}