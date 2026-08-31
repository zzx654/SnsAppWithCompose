package com.androiddev.snsappwithcompose.common.util

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.util.Constants.NONE
import com.androiddev.snsappwithcompose.R

fun generateDisplayName(context: Context, nickname:String, anonymous:String?):String {
    return    anonymous?.let {
        "${getString(context,R.string.anonymous)} [$it]"
    } ?: nickname
}