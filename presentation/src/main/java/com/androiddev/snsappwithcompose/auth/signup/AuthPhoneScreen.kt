package com.androiddev.snsappwithcompose.auth.signup

import android.annotation.SuppressLint
import android.os.Build
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.Constants.AUTH_LIMITEDTIME
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.components.AuthNumberTextField
import com.androiddev.snsappwithcompose.auth.components.AuthTextField
import com.androiddev.snsappwithcompose.auth.components.BottomButton
import com.androiddev.snsappwithcompose.util.Screen
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthPhoneScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
) {
    var args = navBackStackEntry.toRoute<Screen.AuthPhoneScreen>()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
    }
    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                TopAppBar(
                    title = { Text(text = "휴대폰 인증") },

                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack()}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()

    ) { contentPadding ->

        println("플랫폼:${args.platform}아이디${args.id}")
        println("리컴포즈1")
        Column(modifier = Modifier
            .imePadding()
        ) {
            println("리컴포즈2")
            val scrollState = rememberScrollState()
            Column(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(contentPadding)
                .verticalScroll(scrollState)) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = stringResource(R.string.auth_phone), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(50.dp))
                Text(text = stringResource(R.string.enter_phone),fontSize = 13.sp)
                Spacer(modifier = Modifier.height(13.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)) {

                    AuthTextField(
                        modifier = Modifier
                            .weight(5f)
                            .fillMaxHeight() ,
                        text = {"" },
                        onTextChange = { },
                        keyboardType = KeyboardType.Number
                    ).also{println("폰넘버 리컴포즈")}

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(   colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight(),shape = RoundedCornerShape(4.dp),onClick = {}) {
                        Text(
                            text = stringResource(R.string.send_authcode),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }.also{}
                Spacer(modifier = Modifier.height(20.dp))
                AuthNumberTextField(
                    isNumberReceived = { false },
                    limitTime = { AUTH_LIMITEDTIME },
                    number = { "" },
                    onNumberChange = {  },
                    hint = stringResource(R.string.enter_authcode),
                    inCorrect = { false }
                )
            }
            BottomButton(
                buttonText = stringResource(R.string.authenticate),
                activeButton = { false },
                onClick = {}
            )
        }
    }
}