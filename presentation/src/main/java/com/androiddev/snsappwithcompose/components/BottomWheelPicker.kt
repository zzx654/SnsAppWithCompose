package com.androiddev.snsappwithcompose.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import com.androiddev.snsappwithcompose.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun BottomWheelPicker(
    pickerMaxHeight: Int = 250,
    initValue: ()->Int,
    min: Int,
    max: Int,
    showDialog:()-> Boolean,
    onClickConfirm: (Int) -> Unit,
    onClickCancel:()->Unit
) {
    if(showDialog()) {
        val context = LocalContext.current
        val modalBottomSheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initValue() - min)
        val selectedValue by remember { derivedStateOf { lazyListState.firstVisibleItemIndex + min } }
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = getString(context,R.string.set_birth),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .scale(
                                scaleX = 1.1f,
                                scaleY = 1.1f
                            ).align(Alignment.Center)
                    )
                    Text(
                        text = getString(context,R.string.confirm),
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 10.dp)
                            .clickable{
                                scope.launch {
                                    modalBottomSheetState.hide()
                                }.invokeOnCompletion { onClickConfirm(selectedValue) }
                            }
                    )
                }

                HorizontalDivider(thickness = 1.dp,color = Color.Black.copy(alpha = 0.1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 16.dp, 4.dp)
                ) {

                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(16.dp, (pickerMaxHeight/5*2).dp),
                        flingBehavior = rememberSnapFlingBehavior(lazyListState),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(pickerMaxHeight.dp)
                    ) {
                        items((min..max).toList()) { item ->
                            val curTextIsCenter = selectedValue == item
                            val curTextIsCenterDiffer = item == selectedValue - 1 || item == selectedValue + 1
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((pickerMaxHeight/5).dp)
                            ) {
                                Text(
                                    text = "$item",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .scale(
                                            scaleX = if (curTextIsCenter) 1.1f else if (curTextIsCenterDiffer) 1.0f else 0.8f,
                                            scaleY = if (curTextIsCenter) 1.1f else if (curTextIsCenterDiffer) 1.0f else 0.8f
                                        ),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black.takeIf { item == selectedValue } ?: Color(0xFFD0D0D0),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(pickerMaxHeight.dp * 0.225f)
                            .background(Color.Black.copy(alpha = 0.03f))
                    )
                }
            }
        }
    }
}
