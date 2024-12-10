package com.androiddev.snsappwithcompose.auth.signin


import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.components.AuthTextField
import com.androiddev.snsappwithcompose.auth.components.KakaoSignInButton
import com.androiddev.snsappwithcompose.auth.components.NaverSignInButton
import com.androiddev.snsappwithcompose.auth.components.OutlinedTextFieldBackground
import com.androiddev.snsappwithcompose.components.AlertDialog
import com.androiddev.snsappwithcompose.components.LoadingDialog
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SignInScreen(navController: NavController,viewModel: SignInViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {
                    navController.navigate(event.screen)
                }
            }
        }
    }
    LoadingDialog {
        viewModel.isLoading.value
    }
    AlertDialog(
        title = {viewModel.alertDialogState.value.title},
        cancelText = {viewModel.alertDialogState.value.cancelText},
        confirmText = {viewModel.alertDialogState.value.confirmText},
        onClickConfirm = viewModel.alertDialogState.value.onClickConfirm,
        onClickCancel = viewModel.alertDialogState.value.onClickCancel
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .size(120.dp),
                painter = painterResource(id = R.drawable.dog),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(7f)
                .padding(horizontal = 30.dp)

        ) {
            Text(text = stringResource(R.string.signin), fontWeight = FontWeight.Bold,fontSize = 13.sp,modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 10.dp))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextFieldBackground(color = Color.White ) {
                AuthTextField(modifier = Modifier.fillMaxWidth(), text = { viewModel.account.value }, onTextChange = {viewModel.onEvent(SignInEvent.TypeAccount(it))} , keyboardType = KeyboardType.Email,hint = stringResource(R.string.email_hint) )
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextFieldBackground(color = Color.White) {
                AuthTextField(modifier = Modifier.fillMaxWidth(), text = { viewModel.password.value }, onTextChange = {viewModel.onEvent(SignInEvent.TypePwd(it))} , keyboardType = KeyboardType.Password,hint = stringResource(R.string.password_hint) )
            }

            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.find_id), fontWeight = FontWeight.Bold,fontSize = 13.sp)
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = stringResource(R.string.find_pw), fontWeight = FontWeight.Bold,fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),onClick = { viewModel.onEvent(SignInEvent.emailSignIn) },shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.width(50.dp),color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.social_login), fontWeight = FontWeight.Bold,fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(modifier = Modifier.width(50.dp),color = Color.Gray, thickness = 1.dp)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                KakaoSignInButton(
                    onKaKaoSignInCompleted = { account -> viewModel.onEvent(SignInEvent.socialSignIn(getString(context,R.string.kakao),account)) },
                    onError =  { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.width(20.dp))
                NaverSignInButton(
                    onNaverSignInCompleted = { account -> viewModel.onEvent(SignInEvent.socialSignIn(getString(context,R.string.naver),account)) } ,
                    onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.no_account), fontWeight = FontWeight.Bold,fontSize = 13.sp,color = Color.Gray)
                Spacer(modifier = Modifier.width(15.dp))
                Text(modifier = Modifier.clickable {  navController.navigate(Screen.AuthPhoneScreen(platform = getString(context,R.string.email),account = null))},text = stringResource(R.string.create_account), fontWeight = FontWeight.Bold,fontSize = 13.sp,color = Color.Black)
            }
        }
    }
}
