package com.androiddev.snsappwithcompose.feature.auth.signin


import android.Manifest
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.viewmodel.CurrentUserViewModel
import com.androiddev.snsappwithcompose.feature.auth.components.KakaoSignInButton
import com.androiddev.snsappwithcompose.feature.auth.components.NaverSignInButton
import com.androiddev.snsappwithcompose.feature.auth.components.SignInTextField
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.NotificationPermissionUtils
import com.androiddev.snsappwithcompose.common.util.addFocusCleaner
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SignInScreen(
    navController: NavController,
    currentUserViewModel: CurrentUserViewModel,
    signinViewModel: SignInViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
    }
    LaunchedEffect(key1 = true) {
        signinViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }

                is UiEvent.navigate -> {
                    event.userId?.let {
                        currentUserViewModel.setUserId(it)
                    }
                    navController.navigate(event.screen)
                }
                else -> null
            }
        }
    }
    LaunchedEffect(key1 = true) {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onUnGranted = {
                launcherMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }
        )

    }
    LoadingDialog {
        signinViewModel.isLoading.value
    }
    AlertDialog(
        title = { signinViewModel.alertDialogState.value.title },
        cancelText = { signinViewModel.alertDialogState.value.cancelText },
        confirmText = { signinViewModel.alertDialogState.value.confirmText },
        onClickConfirm = signinViewModel.alertDialogState.value.onClickConfirm,
        onClickCancel = signinViewModel.alertDialogState.value.onClickCancel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .addFocusCleaner(focusManager)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .size(100.dp),
                painter = painterResource(id = R.drawable.dog),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 30.dp)

        ) {

            Text(
                text = stringResource(R.string.signin),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            SignInTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = { signinViewModel.account.value },
                focusManager = focusManager,
                onDone = { focusManager.moveFocus(FocusDirection.Next) },
                onTextChange = { signinViewModel.onEvent(SignInEvent.TypeAccount(it)) },
                keyboardType = KeyboardType.Email,
                hint = stringResource(R.string.email_hint)
            )
            Spacer(modifier = Modifier.weight(0.2f))
            SignInTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = { signinViewModel.password.value },
                focusManager = focusManager,
                onTextChange = { signinViewModel.onEvent(SignInEvent.TypePwd(it)) },
                keyboardType = KeyboardType.Password,
                hint = stringResource(R.string.password_hint)
            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = stringResource(R.string.find_id),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = stringResource(R.string.find_pw),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = { signinViewModel.onEvent(SignInEvent.EmailSignIn) },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 30.dp)

        ) {


            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.width(50.dp),
                    color = Color.Gray,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.social_login),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(
                    modifier = Modifier.width(50.dp),
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                KakaoSignInButton(
                    onKaKaoSignInCompleted = { account ->
                        signinViewModel.onEvent(
                            SignInEvent.SocialSignIn(
                                getString(context, R.string.kakao),
                                account
                            )
                        )
                    },
                    onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.width(20.dp))
                NaverSignInButton(
                    onNaverSignInCompleted = { account ->
                        signinViewModel.onEvent(
                            SignInEvent.SocialSignIn(
                                getString(context, R.string.naver),
                                account
                            )
                        )
                    },
                    onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.no_account),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    modifier = Modifier.clickable {
                        navController.navigate(
                            Screen.AuthPhoneScreen(
                                platform = getString(context, R.string.email),
                                account = null
                            )
                        )
                    },
                    text = stringResource(R.string.create_account),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }
        }
    }

}
