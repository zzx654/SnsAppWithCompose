package com.androiddev.snsappwithcompose.auth.components

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.androiddev.snsappwithcompose.R
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

@Composable
fun NaverSignInButton(
    onNaverSignInCompleted: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(), onResult = {
        when(it.resultCode) {
            Activity.RESULT_OK -> {

                NidOAuthLogin().callProfileApi( object : NidProfileCallback<NidProfileResponse> {
                    override fun onError(errorCode: Int, message: String) {
                        onError(message)
                        Log.e("CallProfileErr", "message :  ${message}")
                    }
                    override fun onFailure(httpStatus: Int, message: String) {
                        onError(message)
                        Log.e("CallProfileFailure", "message: $message")
                    }
                    override fun onSuccess(result: NidProfileResponse) {
                        Log.i("CallProfileSuccess", "result.profile :  ${result.profile?.id}")
                        result.profile?.id?.let{ onNaverSignInCompleted(it) }
                    }
                })
            }
            Activity.RESULT_CANCELED -> {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Log.e("NaverSignErr","errorCode:$errorCode, errorDesc:$errorDescription")
                if (errorDescription != null) {
                    onError(errorDescription)
                }
            }
        }
    })
    SocialMediaLogIn(
        icon = R.drawable.naver_logo,
        onClick = { NaverIdLoginSDK.authenticate(context, launcher) }
    )
}
