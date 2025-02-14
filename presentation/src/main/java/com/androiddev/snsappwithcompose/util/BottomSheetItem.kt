package com.androiddev.snsappwithcompose.util


import androidx.annotation.DrawableRes

data class BottomSheetItem (
    @DrawableRes val icon: Int,
    val text: String,
    val onClick: ()->Unit
)