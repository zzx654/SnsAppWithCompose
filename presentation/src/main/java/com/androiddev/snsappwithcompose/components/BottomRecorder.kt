package com.androiddev.snsappwithcompose.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val elapsed by viewModel.elapsedTime.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val formattedTime = "%02d:%02d".format(elapsed / 60, elapsed % 60)
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
                            viewModel.stopRecording(context)
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion { onClickCancel()  }

                    }
                )
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(progress = {progress},modifier = Modifier.size(130.dp),color = Color.Black)

                        Icon(
                            imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.Mic,
                            //if (isRecording) Icons.Default.Pause else Icons.Default.Mic,
                            contentDescription = "Record",
                            tint = Color.Black,
                            modifier = Modifier.size(120.dp).clickable{
                                if (isRecording) viewModel.stopRecording(context)
                                else viewModel.startRecording(context)
                            }
                            .background(if (isRecording) Color.Red else Color.Gray, CircleShape)
                        )

                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = formattedTime, fontWeight = FontWeight.Bold,color = Color.Black)
                }
                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Green,    modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp).clickable { viewModel.stopRecording(context)  })

            }
        }

    }



}