package com.androiddev.data.repository.postdetail

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.postdetail.PostApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: PostApi
): PostRepository {
    override suspend fun deletePost(postId: Int): Flow<Resource<Unit>> =
        safeApiCall(
            context = context,
            apiCall = { api.deletePost(postId) },
            mapToResource = {}
        )
    override suspend fun getPost(
        postId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<Posts>> =  safeApiCall(
        context = context,
        apiCall = { api.getPost(postId,latitude,longitude) },
        mapToResource = { it.toPosts() }
    )


}