package com.androiddev.data.repository.postlist

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.postlist.GetPostsApi
import com.androiddev.data.remote.dto.toGetPostsResponse
import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.postlist.GetPostsRepository
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
    override suspend fun getNewTagPosts(
        postId: Int?,
        postDate:String?,
        tagId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<GetPostsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.getNewTagPosts(postId,postDate,tagId,latitude,longitude).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getNewTagPostsresult = result.toGetPostsResponse(posts = result.posts, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(getNewTagPostsresult))
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
    override suspend fun getPopularTagPosts(
        postId: Int?,
        tagId: Int,
        score: Double?,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<GetPostsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.getPopularTagPosts(postId,tagId,score,latitude,longitude).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getPopularTagPostsresult = result.toGetPostsResponse(posts = result.posts, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(getPopularTagPostsresult))
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

    override suspend fun getNearPosts(
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
    override suspend fun getNewPosts(
        postId: Int?,
        postDate: String?,
        latitude: Double,
        longitude: Double
    ): Flow<Resource<GetPostsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.getNewPosts(postId,postDate,latitude,longitude).body()?.let { result ->
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


    override suspend fun getSelectedPost(
        postId: Int,
        latitude: Double?,
        longitude: Double?
    ): Flow<Resource<GetPostsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.getSelectedPost(postId,latitude,longitude).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getSelectedPostresult = result.toGetPostsResponse(posts = result.posts, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(getSelectedPostresult))
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