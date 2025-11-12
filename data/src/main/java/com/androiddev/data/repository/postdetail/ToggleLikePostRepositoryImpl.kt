package com.androiddev.data.repository.postdetail

import android.content.Context

import com.androiddev.data.remote.api.postdetail.ToggleLikePostApi

import com.androiddev.data.remote.dto.toToggleLikeResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.ToggleLikeResult
import com.androiddev.domain.repository.postdetail.ToggleLikePostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikePostRepositoryImpl @Inject constructor(
    private val api: ToggleLikePostApi,
    private val context: Context
) : ToggleLikePostRepository {

    override suspend fun toggleLikePost(postid: Int): Flow<Resource<ToggleLikeResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.toggleLikePost(postid) },
            mapToResource = { it.toToggleLikeResult(isLiked = it.isLiked) }
        )

}