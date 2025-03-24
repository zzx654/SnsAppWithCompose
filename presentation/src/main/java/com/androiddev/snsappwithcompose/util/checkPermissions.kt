package com.androiddev.snsappwithcompose.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

fun checkPermissions(
    context: Context,
    permissions: Array<String>,
    //launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = null,
    onGranted: () -> Unit = {},
    onUnGranted: () -> Unit = {}
) {

    /** 권한이 이미 있는 경우 **/
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        onGranted()
        Log.d("test5", "권한이 이미 존재합니다.")
    }

    /** 권한이 없는 경우 **/
    else {
        //launcher?.launch(permissions)
        onUnGranted()
        Log.d("test5", "권한을 요청하였습니다.")
    }
}