package com.androiddev.snsappwithcompose.common.model

data class MenuItem(
    val label: String,
    val onClick: () -> Unit
)