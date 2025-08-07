package com.androiddev.snsappwithcompose.util

data class MenuItem(
    val label: String,
    val onClick: () -> Unit
)