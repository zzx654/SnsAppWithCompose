package com.androiddev.data.repository.postdetail

import com.androiddev.data.remote.api.postdetail.PostApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Post
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.util.DataError
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val api: PostApi
): PostRepository {
    private val _postDetailState = MutableStateFlow<Post?>(null)
    override val postDetailState: StateFlow<Post?> = _postDetailState.asStateFlow()
    override suspend fun deletePost(postId: Int): Flow<Resource<Unit>> =
        safeApiCall(
            apiCall = { api.deletePost(postId) },
            mapToResource = {}
        )
    override suspend fun getPost(
        postId: Int,
        latitude:Double?,
        longitude:Double?
    ): Flow<Resource<List<Post>>> = flow {
        try {
            emit(Resource.Loading())
            val response =  api.getPost(
                postid = postId,
                latitude = latitude,
                longitude = longitude
            )

            response.body()?.let { result ->
                val tokenValid = result.isTokenValid ?: true

                if (!tokenValid) {
                    emit(Resource.TokenExpired())
                } else if (result.resultCode == 200) {
                    val posts = result.data?.toPosts()
                    posts?.let {
                        if(it.isNotEmpty())
                            _postDetailState.value = it[0]
                    }
                    emit(Resource.Success(posts))

                } else {
                    emit(Resource.Error(DataError.Network.SERVER_ERROR))
                }
            }
        } catch (e: IOException) {
            emit(Resource.Error(DataError.Network.CONNECTION_ERROR))
        } catch (e: Exception) {
            emit(Resource.Error(DataError.Network.UNEXPECTED_ERROR))
        }

    }

    override fun getCachedPost(postId: Int): Post? {
        val current = _postDetailState.value
        return if (current?.postId == postId) current else null
    }

    override fun updateCachedPost(updatedPost: Post) {
        _postDetailState.value = updatedPost
    }




}