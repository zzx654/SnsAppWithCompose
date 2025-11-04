package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.UploadPostApi
import com.androiddev.data.remote.dto.toGetPostsResponse
import com.androiddev.data.remote.dto.toGetTagsResponse
import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.model.Tag
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.repository.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UploadPostRepositoryImpl @Inject constructor(
    private val api: UploadPostApi,
    private val context: Context
): UploadPostRepository {


    override suspend fun uploadPost(
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        images: List<MultipartBody.Part>?,
        audio:MultipartBody.Part?,
        voteOptions:RequestBody?,
        text: RequestBody,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?
    ): Flow<Resource<Unit>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.uploadPost(anonymousNick,tags,images,audio,voteOptions,text,latitude,longitude).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(Unit))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }
        }
    }

    override suspend fun editPost(
        postid: MultipartBody.Part,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?,
        anonymousNick: RequestBody?,
        deleteImages: RequestBody?,
        tags: RequestBody?,
        image: List<MultipartBody.Part>?,
        audio: MultipartBody.Part?,
        deleteAudio: RequestBody?,
        text: RequestBody
    ): Flow<Resource<GetPostsResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.editPost(postid,latitude,longitude,anonymousNick,deleteImages,tags,image,audio,deleteAudio,text).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        val editResult = result.toGetPostsResponse(posts = result.posts, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(editResult))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }
        }
    }

}
