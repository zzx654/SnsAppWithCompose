package com.androiddev.data.remote.dto

import com.androiddev.domain.model.UploadImageResponse


data class UploadImageResponseDto (
    val resultCode: Int,
    val imageUrl: String
)
fun UploadImageResponseDto.toUploadImageResponse(imageUrl: String) : UploadImageResponse {
    return UploadImageResponse(imageUrl = imageUrl)
}