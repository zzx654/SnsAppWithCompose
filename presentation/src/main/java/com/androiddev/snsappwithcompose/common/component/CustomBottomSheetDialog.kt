package com.androiddev.snsappwithcompose.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.snsappwithcompose.common.model.BottomSheetItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDialog(
    showDialog:()->Boolean,
    items:()->List<BottomSheetItem>,
    onClickCancel:()->Unit
) {
    if(showDialog()) {
        val modalBottomSheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = { onClickCancel() },
            sheetState = modalBottomSheetState,
            dragHandle = null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                items().forEach { item->

                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,

                        modifier = Modifier
                            .fillMaxWidth().offset(x = (-4).dp)
                            .padding(20.dp)
                            .clickable { scope.launch{
                                modalBottomSheetState.hide()
                            }.invokeOnCompletion { item.onClick() } }){
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = item.icon),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.text,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}