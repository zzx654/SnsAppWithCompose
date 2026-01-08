package com.androiddev.snsappwithcompose.feature.home.user

import com.androiddev.domain.model.User


data class GetUsersState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
)