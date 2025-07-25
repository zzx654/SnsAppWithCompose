package com.androiddev.domain.model

data class GetCommentsResponse(
    val comments:List<Comment>,
    val isTokenValid:Boolean,
)