package com.androiddev.snsappwithcompose.common.util

import com.google.firebase.messaging.FirebaseMessaging

fun withFcmToken(block: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        val token = task.result ?: return@addOnCompleteListener
        block(token)
    }
}