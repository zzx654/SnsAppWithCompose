package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.GetPostsApi
import com.androiddev.data.remote.dto.toGetPostsResponse
import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPostsRepositoryImpl @Inject constructor(
    private val api: GetPostsApi,
    private val context: Context
): GetPostsRepository {
    override suspend fun GetNearPosts(
        postId: Int?,
        postDate: String?,
        maxDistance: Int,
        latitude: Double,
        longitude: Double
    ): Flow<Resource<GetPostsResponse>> {
       return flow {
           try{
               emit(Resource.Loading())
               api.getNearPosts(postId,postDate,maxDistance,latitude,longitude).body()?.let { result ->
                   if(result.resultCode == 200) {
                       val getNearPostsresult = result.toGetPostsResponse(posts = result.posts, isTokenValid = result.isTokenValid)
                       emit(Resource.Success(getNearPostsresult))
                   } else {
                       emit(Resource.Error(getString(context,R.string.server_error)))
                   }
               }
           } catch(e: HttpException) {
               emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

           } catch(e: IOException) {
               emit(Resource.Error(getString(context,R.string.connection_error)))
           }
       }
    }

}