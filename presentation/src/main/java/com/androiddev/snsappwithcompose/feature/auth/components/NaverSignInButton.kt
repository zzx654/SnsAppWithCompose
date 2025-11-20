package com.androiddev.snsappwithcompose.feature.auth.components

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.androiddev.snsappwithcompose.R
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.util.NidProfileCallback

@Composable
fun NaverSignInButton(
    onNaverSignInCompleted: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(), onResult = {
        when(it.resultCode) {
            RESULT_OK -> {
                NidOAuth.getUserProfile(
                    object : NidProfileCallback<NidProfile> {
                        override fun onSuccess(result: NidProfile) {
                            Log.i("CallProfileSuccess", "result.profile :  ${result.profile.id}")
                            onNaverSignInCompleted(result.profile.id)
                        }
                        override fun onFailure(
                            errorCode: String,
                            errorDesc: String,
                        ) {
                            onError("errorCode:$errorCode, errorDesc:$errorDesc")
                        }
                    },
                )
            }
            RESULT_CANCELED -> {
                val errorCode = NidOAuth.getLastErrorCode().code
                val errorDescription = NidOAuth.getLastErrorDescription()
                onError("errorCode:$errorCode, errorDesc:$errorDescription")
            }
        }
    })
    SocialMediaLogIn(
        icon = R.drawable.naver_logo,
        onClick = { NidOAuth.requestLogin(context, launcher)}
    )
}
