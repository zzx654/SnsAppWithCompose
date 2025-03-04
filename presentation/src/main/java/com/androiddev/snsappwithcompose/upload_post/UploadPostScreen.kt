package com.androiddev.snsappwithcompose.upload_post

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.components.BottomButton
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.ScreenWithTopBar
import com.androiddev.snsappwithcompose.components.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UploadPostScreen(navController: NavController,viewModel: UploadPostViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    ScreenWithTopBar(
        focusManager = focusManager,
        topBar = {
            CenterAlignedTopBar(
                title = stringResource(R.string.upload_post),
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton( onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content =  {

            Spacer(modifier = Modifier.height(30.dp))
            SearchTextField(
                modifier = Modifier.fillMaxWidth(),
                text = { viewModel.tagTextField.value },
                onTextChange = { viewModel.onEvent(UploadPostEvent.TypeTag(it))},
                hint = "태그를 검색해 보세요"
            )
        },
        bottomBar = {
            BottomButton(
                buttonText = stringResource(id = R.string.request_signup),
                activeButton = { true
                },
                onClick = { }
            )
        }
    )
}