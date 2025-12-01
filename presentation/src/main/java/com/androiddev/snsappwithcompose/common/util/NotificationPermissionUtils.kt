package com.androiddev.snsappwithcompose.common.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat


object NotificationPermissionUtils {
    fun checkNotificationPermission(
        context: Context,
        onGranted: () -> Unit = {},
        onUnGranted: () -> Unit = {}
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상
            if (isNotificationPermissionGranted(context)) {
                onGranted()
            } else {
                onUnGranted()
            }
        } else {
            // Android 13 미만
            onGranted()
        }
    }


    // 알림 권한이 허용되었는지 확인하는 함수
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    // 알림 권한 요청 함수
    fun requestNotificationPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                requestCode
            )
        }
    }
}