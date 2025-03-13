package com.androiddev.snsappwithcompose.util

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

fun Context.FindActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.FindActivity()
    else -> null
}
