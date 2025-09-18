package com.androiddev.domain.model

data class CancelVoteResponse (
    val isTokenValid:Boolean,
    val resultCode: Int
)