package com.androiddev.domain.model

data class AuthCodeResponse(
    val resultCode:Int,
    val isCorrect:Boolean
)