package com.androiddev.snsappwithcompose.feature.PostDetail.vote.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PollCard(
    voteState: VoteState,
    onOptionSelected:(Int) ->Unit,
    onVoteClick:() ->Unit
) {
    val context = LocalContext.current
    if(voteState.voteOptions.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VoteChartIconInCircle()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = getString(context, R.string.anonymous_voting),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
                if(!voteState.hasVoted&&!voteState.isMyPost) {

                    voteState.voteOptions.forEachIndexed { index, option ->
                        val isLast = index == voteState.voteOptions.lastIndex

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (!isLast) Modifier.padding(bottom = 18.dp) else Modifier // 항목 사이에만 패딩
                                )
                                .clickable { onOptionSelected(option.optionId) }
                        ) {
                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                                RadioButton(
                                    selected = voteState.selectedChoiceId == option.optionId,
                                    onClick = { onOptionSelected(option.optionId) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color.Black,
                                        unselectedColor = Color.Gray
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option.optionText)
                        }
                    }

                } else {
                    voteState.voteOptions.forEach { result ->
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)) {

                            Text(
                                text = "${result.optionText} (${result.voteCount}표, ${result.percentage}%)",
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            RoundedLinearProgressIndicator(
                                progress = (result.percentage / 100).toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color.Black,
                                backgroundColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
                if(!voteState.isMyPost) {
                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        onClick = onVoteClick,
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,     // 기본 배경색
                            contentColor = Color.White        // 텍스트/Icon 색
                        )
                    ) {
                        Text(text = getString(context,if(voteState.hasVoted)R.string.vote_again else R.string.vote) ,color = Color.White)
                    }
                } else {
                    Spacer(modifier = Modifier.height(7.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }


}

@Composable
fun VoteChartIconInCircle() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color = Color.Black, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.BarChart, // 원하는 아이콘으로 변경 가능
            contentDescription = "Vote Chart",
            tint = Color.White,
            modifier = Modifier.size(15.dp)
        )
    }
}