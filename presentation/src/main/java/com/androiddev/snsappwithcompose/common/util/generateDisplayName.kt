package com.androiddev.snsappwithcompose.common.util

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.util.Constants.NONE
import com.androiddev.snsappwithcompose.R

fun generateDisplayName(context: Context, nickname:String, anonymous:String?):String {
    var nick = ""
    nick = nickname

    anonymous?.let {
        if(it!= NONE) {
            nick = "${getString(context,R.string.anonymous)}[${it}]"
        }
    }
    return nick
}