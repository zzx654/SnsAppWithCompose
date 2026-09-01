package com.androiddev.data.repository.uploadpost

import android.content.Context
import android.net.Uri
import com.androiddev.data.remote.api.uploadpost.UploadPostApi
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.generateAnonymousNickname
import com.androiddev.data.util.getMultipartBody
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.MediaType
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.UploadPostParam
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
import com.androiddev.domain.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class UploadPostRepositoryImpl @Inject constructor(
    private val api: UploadPostApi,
    private val context: Context
): UploadPostRepository {


    override suspend fun uploadPost(
        param: UploadPostParam,
        location: LocationState,
        /**anonymousNick: RequestBody?,
        tags: RequestBody?,
        media: List<MultipartBody.Part>?,
        mediaTypes:List<RequestBody>?,
        voteOptions:RequestBody?,
        text: RequestBody,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?**/
    ): Flow<Resource<Unit>> = safeApiCall (
        context = context,
        apiCall= {
            val voteOptionsJson = param.voteOptions.takeIf { it.isNotEmpty() }?.let {
                val json = Gson().toJson(it.map { VoteOptionData(voteoption = it) })
                json.toRequestBody("application/json".toMediaType())
            }
            val body = buildRequestBodies(
                param = param,
                lat = location.latitude,
                lon = location.longitude
            )
            api.uploadPost(
                anonymousNick = if(param.isAnonymous)
                    generateAnonymousNickname().toRequestBody("text/plain".toMediaTypeOrNull())
                else null,
                tags = body.tags,
                media = body.media,
                mediaTypes = body.mediaTypes,
                text = body.text,
                voteOptions = voteOptionsJson,
                latitude = body.latitude,
                longitude = body.longitude
            )
        },
        mapToResource = {
        }
    )



    override suspend fun editPost(
        postid: Int,
        param: UploadPostParam,
        deletedVisualMedia:List<String>,
        deletedAudio:String?
        //latitude: MultipartBody.Part?,
        //longitude: MultipartBody.Part?,
        //anonymousNick: RequestBody?,
        //tags: RequestBody?,
        //media: List<MultipartBody.Part>?,
        //mediaTypes:List<RequestBody>?,
        //deletedVisualMedia: RequestBody?,
        //deletedAudio: RequestBody?,
        //text: RequestBody
    ): Flow<Resource<List<Post>>> = safeApiCall(
        context = context,
        apiCall = {
            val body = buildRequestBodies(param)
            val deletedVisualMediaJson = Gson().toJson(deletedVisualMedia)
            val deletedVisualMediaBody =
                deletedVisualMediaJson.toRequestBody("application/json".toMediaTypeOrNull())
            api.editPost(
                postid = MultipartBody.Part.createFormData("postid", postid.toString()),
                latitude = body.latitude,
                longitude = body.longitude,
                anonymousNick = if(param.isAnonymous)
                    generateAnonymousNickname().toRequestBody("text/plain".toMediaTypeOrNull())
                else null,
                tags = body.tags,
                media = body.media,
                mediaTypes = body.mediaTypes,
                deletedVisualMedia = deletedVisualMediaBody,
                deletedAudio = deletedAudio?.toRequestBody("text/plain".toMediaTypeOrNull()),
                text = body.text
            )
        },
        mapToResource = { it.toPosts() }
    )
    private fun buildRequestBodies(
        param: UploadPostParam,
        lat: Double? = null,
        lon: Double? = null
    ): UploadRequestData {
        val requestTags = if (param.tags.isNotEmpty()) {
            param.tags.joinToString("#").toRequestBody("text/plain".toMediaTypeOrNull())
        } else null

        val requestText = param.text.toRequestBody("text/plain".toMediaTypeOrNull())
        val mediaParts = mutableListOf<MultipartBody.Part>()
        val mediaTypes = mutableListOf<RequestBody>()

        // 로컬/신규 미디어 파일 Multipart 처리
        param.mediaItems.filter { it.isNew }.forEach { media ->
            media.uri?.let { uri ->
                val part = getMultipartBody(uri = uri, context = context, type = media.type)
                mediaParts.add(part)
                mediaTypes.add(media.type.name.toRequestBody("text/plain".toMediaTypeOrNull()))
            }
        }

        // 음성 파일 Multipart 처리
        param.audioPath?.let { path ->
            val part = getMultipartBody(path = path, context = context, type = MediaType.AUDIO)
            mediaParts.add(part)
            mediaTypes.add(MediaType.AUDIO.name.toRequestBody("text/plain".toMediaTypeOrNull()))
        }

        val requestLat = lat?.let { MultipartBody.Part.createFormData("latitude", it.toString()) }
        val requestLong = lon?.let { MultipartBody.Part.createFormData("longitude", it.toString()) }



        return UploadRequestData(
            tags = requestTags,
            text = requestText,
            media = mediaParts.ifEmpty { null },
            mediaTypes = mediaTypes.ifEmpty { null },
            latitude = requestLat,
            longitude = requestLong
        )
    }
}
internal data class UploadRequestData(
    val tags: RequestBody?,
    val text: RequestBody,
    val media:List<MultipartBody.Part>?,
    val mediaTypes:List<RequestBody>?,
    val latitude: MultipartBody.Part?,
    val longitude: MultipartBody.Part?
)
internal data class VoteOptionData(
    val voteoption: String
)
