package com.androiddev.snsappwithcompose.auth.signup

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.Constants.PASSWORD_REGEX
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.components.AuthNumberTextField
import com.androiddev.snsappwithcompose.auth.components.AuthTextField
import com.androiddev.snsappwithcompose.auth.components.BottomButton
import com.androiddev.snsappwithcompose.auth.components.CheckPassword
import com.androiddev.snsappwithcompose.components.LoadingDialog
import com.androiddev.snsappwithcompose.util.UiEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmailSignUpScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    viewModel: EmailSignUpViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val limitTime by viewModel.limitTime.collectAsState()
    LoadingDialog {
        viewModel.isLoading.value
    }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {//네비게이션
                    navController.navigate(event.screen)

                }
            }
        }
    }
    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.signup),fontWeight = FontWeight.Bold,fontSize = 16.sp) },

                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()

    ) { contentPadding ->
        val scrollState = rememberScrollState()
        //Column
        Column(modifier = Modifier
            .imePadding()
        ) {
            Column(modifier = Modifier
                .imePadding()
            ) {
                Column(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(contentPadding)
                    .verticalScroll(scrollState)) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(text = stringResource(R.string.enter_email), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(13.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)) {
                        AuthTextField(
                            modifier = Modifier
                                .weight(5f)
                                .fillMaxHeight() ,
                            text = {
                                viewModel.email.value /*TODO*/ },
                            onTextChange = { viewModel.onEvent(EmailSignUpEvent.TypeEmail(it)) },
                            keyboardType = KeyboardType.Email)

                        Spacer(modifier = Modifier.width(10.dp))
                        Button(   colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),modifier = Modifier
                            .weight(3f)
                            .fillMaxHeight(),shape = RoundedCornerShape(4.dp),onClick = {viewModel.onEvent(EmailSignUpEvent.RequestAuthCode) }) {
                            Text(
                                text = if(viewModel.isCodeReceived.value)stringResource(R.string.resend_authcode) else stringResource(R.string.send_authcode),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    AuthNumberTextField(
                        isNumberReceived = { viewModel.isCodeReceived.value },
                        limitTime = { limitTime },
                        number = { viewModel.authCodeField.value.code },
                        onNumberChange = { viewModel.onEvent(EmailSignUpEvent.TypeAuthCode(it)) },
                        hint = stringResource(id = R.string.enter_authcode),
                        inCorrect = { viewModel.authCodeField.value.isError }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(text = stringResource(id = R.string.enter_password), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(13.dp))
                    AuthTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = { viewModel.password.value /*TODO*/ },
                        onTextChange = { viewModel.onEvent(EmailSignUpEvent.TypePwd(it)) },
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    AuthTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = { viewModel.repeatPw.value /*TODO*/ },
                        onTextChange = { viewModel.onEvent(EmailSignUpEvent.TypeRepeatPwd(it)) },
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    CheckPassword {
                        viewModel.password.value == viewModel.repeatPw.value &&
                                Pattern.matches(
                                    PASSWORD_REGEX,
                                    viewModel.password.value
                                )
                    }
                }
                BottomButton(
                    buttonText = stringResource(id = R.string.request_signup),
                    activeButton = {
                        viewModel.password.value == viewModel.repeatPw.value &&
                                viewModel.isCodeReceived.value&&
                                Pattern.matches( PASSWORD_REGEX,viewModel.password.value)&&
                                viewModel.authCodeField.value.code.isNotEmpty() },
                    onClick = {}
                )
            }
        }
    }
}