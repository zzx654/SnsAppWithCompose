package com.androiddev.snsappwithcompose.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.snsappwithcompose.upload_post.RecordEvent
import com.androiddev.snsappwithcompose.upload_post.RecordState
import com.androiddev.snsappwithcompose.upload_post.RecordViewModel
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomRecorder(
    showDialog:()->Boolean,
    onClickCancel:()->Unit,
    viewModel: RecordViewModel

) {
    val context = LocalContext.current
    val recordState by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState()
    //val formattedTime = "%02d:%02d".format(recordState.elapsedMillis / 60, recordState.elapsedMillis % 60)
    if(showDialog()) {
        val modalBottomSheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = { onClickCancel() },
            sheetState = modalBottomSheetState,
            dragHandle = null,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close",modifier = Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .clickable {
                        scope.launch {

                            modalBottomSheetState.hide()
                        }.invokeOnCompletion { onClickCancel()  }

                    }
                )
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(progress = {0f},modifier = Modifier.size(130.dp),color = Color.Black)
                        Icons.Default.PlayArrow
                        Icon(
                            imageVector =
                            if (recordState.state == RecordState.IDLE) Icons.Default.Mic
                            else if(recordState.state == RecordState.RECORDED) Icons.Default.PlayArrow
                            else Icons.Default.Square,
                            contentDescription = "Record",
                            tint = Color.Black,
                            modifier = Modifier.size(100.dp).clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // ← 리플 제거
                            ){
                                viewModel.onEvent(RecordEvent.RecordPlayBack)
                            }

                        )

                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = recordState.formattedTime, fontWeight = FontWeight.Bold,fontSize = 20.sp,color = Color.Black)
                }
                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Black,    modifier = Modifier
                    .align(Alignment.CenterEnd).padding(horizontal = 30.dp)
                    .size(34.dp).clickable {  })

            }
        }

    }



}