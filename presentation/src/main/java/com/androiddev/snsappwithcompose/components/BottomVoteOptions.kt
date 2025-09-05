package com.androiddev.snsappwithcompose.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.BaseScaffold
import com.androiddev.snsappwithcompose.upload_post.CreateVoteEvent
import com.androiddev.snsappwithcompose.upload_post.CreateVoteViewModel
import com.androiddev.snsappwithcompose.util.addFocusCleaner
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomVoteOptions(
    createVoteViewModel: CreateVoteViewModel

) {

    val voteOptions = createVoteViewModel.voteOptions
    if(createVoteViewModel.showBottomVoteDialog.value) {

        var allowHide by remember { mutableStateOf(false) }
        val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
            confirmValueChange = { newState ->
                if (newState == SheetValue.Hidden) {
                    allowHide // 닫기 허용 시 true, 아니면 false
                } else {
                    true
                }
            })
        ModalBottomSheet(
            onDismissRequest = {
                //onClickCancel()
            },
            sheetState = modalBottomSheetState,
            sheetGesturesEnabled = false,
            dragHandle = null,
            modifier = Modifier.fillMaxWidth() // 높이 제한 (선택)

        ){
            val focusManager = LocalFocusManager.current

            Scaffold(
                topBar = {
                    Surface(
                        shadowElevation = 3.dp,
                        color = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        CenterAlignedTopBar(
                            title = "투표",
                            onBackClick = { createVoteViewModel.onEvent(CreateVoteEvent.onCancelClick) },
                            actions = {
                                IconButton(onClick = {
                                    createVoteViewModel.onEvent(CreateVoteEvent.SaveVoteOptions)


                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .addFocusCleaner(focusManager)
                    .background(MaterialTheme.colorScheme.background),
                contentColor = MaterialTheme.colorScheme.onBackground

            ) {  contentPadding ->

                val coroutineScope = rememberCoroutineScope()
  
                val listState = rememberLazyListState()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        //.background(MaterialTheme.colorScheme.background)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .padding(horizontal = 16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        itemsIndexed(voteOptions) { index, text ->
                            Spacer(modifier = Modifier.height(10.dp))
                            ThinBorderTextField(
                                value = text,
                                onValueChange = { newValue ->
                                    createVoteViewModel.onEvent(CreateVoteEvent.TypeVoteOption(index,newValue))

                                   // voteOptions =
                                    //    voteOptions.toMutableList().also { it[index] = newValue }

                                },
                                hint = "보기 ${index + 1}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth() .height(56.dp),  // 높이 맞추기,
                                onClick = {
                                    //focusManager.clearFocus() // 키보드 내림
                                   // voteOptions = voteOptions + ""
                                    createVoteViewModel.onEvent(CreateVoteEvent.OnAddVoteOptionClick)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(voteOptions.size)
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(2.dp, Color.Black)  // 테두리 색상과 두께

                            ) {
                                Text(
                                    "보기 추가",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                        }
                    }
                }
            }




        }
    }

}
@Composable
fun ThinBorderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .border(
                width = 0.7.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(4.dp) // 조금 더 각지게
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .onFocusChanged { it ->
                isFocused = it.isFocused
            }
    ) {
        if (value.isEmpty()) {
            Text(
                text = hint,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
    }
}