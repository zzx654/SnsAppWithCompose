package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BottomSheetItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDialog(
    showDialog:()->Boolean,
    items:()->List<BottomSheetItem>,
    onClickCancel:()->Unit
) {
    val context = LocalContext.current
    if(showDialog()) {
        val modalBottomSheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ModalBottomSheet(
            onDismissRequest = { onClickCancel() },
            sheetState = modalBottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = bottomPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                items().forEach { item->
                    Row(modifier = Modifier.clickable { scope.launch{
                        modalBottomSheetState.hide()
                    }.invokeOnCompletion { item.onClick() } }){
                        Image(
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .size(20.dp),
                            painter = painterResource(id = item.icon),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = item.text,
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