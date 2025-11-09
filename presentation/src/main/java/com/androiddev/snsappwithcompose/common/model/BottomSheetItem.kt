package com.androiddev.snsappwithcompose.common.model


import androidx.annotation.DrawableRes

data class BottomSheetItem (
    @DrawableRes val icon: Int,
    val text: String,
    val onClick: ()->Unit
)