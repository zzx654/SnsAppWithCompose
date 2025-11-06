package com.androiddev.snsappwithcompose.upload_post

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.Tag
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.use_case.UploadPostUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.generateAnonymousNickname
import com.androiddev.snsappwithcompose.util.getMultipartBody
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCases: UploadPostUseCases,
    private val context: Context
): BaseViewModel() {

    //이미지,거리,음성녹음,투표,
    private val _tagTextField = mutableStateOf("")
    val tagTextField: State<String>
        get() = _tagTextField
    private val _searchedTags = mutableStateListOf<Tag>()
    val searchedTags: SnapshotStateList<Tag>
        get() = _searchedTags
    private val _addedTags = mutableStateListOf<String>()
    val addedTags: SnapshotStateList<String>
        get() = _addedTags
    private val _contentTextField = mutableStateOf("")
    val contentTextField: State<String>
        get() = _contentTextField
    private val _anonymous = mutableStateOf(false)
    val anonymous: State<Boolean>
        get() = _anonymous
    private val _selectedImages = mutableStateListOf<EditableImage>()
    val selectedImages: SnapshotStateList<EditableImage>
        get() = _selectedImages
    val deletedImages = mutableStateListOf<String>()
    private val _locationOnOff = mutableStateOf(false)
    val locationOnOff: State<Boolean>
        get() = _locationOnOff
    var postMode: PostMode? = PostMode.CREATE
        private set
    var postId: Int? = null
    var isInitialized = false
        private set

    init {
        if (postMode == PostMode.CREATE) {
            checkPermissions(
                context = context,
                permissions = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                onGranted = { _locationOnOff.value = true },
                onUnGranted = { _locationOnOff.value = false }
            )
        }

    }

    fun initPost(post: PostPreview) {
        if (!isInitialized) {
            postMode = PostMode.EDIT
            postId = post.postId
            post.tags?.let {
                _addedTags.addAll(it)
            }
            _anonymous.value = post.anonymous
            _locationOnOff.value = post.location != null
            post.images?.let {
                _selectedImages.clear()
                _selectedImages.addAll(
                    it.map { path ->
                        EditableImage(remotePath = path, isNew = false)
                    }
                )
            }
            isInitialized = true
        }


    }

    fun onEvent(event: UploadPostEvent) {
        when (event) {
            is UploadPostEvent.TypeTag -> {
                _tagTextField.value = event.tag
                if (event.tag.isNotEmpty()) {
                    viewModelScope.launch {
                        delay(50L)
                        uploadPostUseCases.searchTag(event.tag)
                            .collect { result ->
                                when (result) {
                                    is Resource.Success -> {

                                        result.data?.let {
                                            if (tagTextField.value.isNotEmpty()) {
                                                _searchedTags.clear()
                                                if (it.tags.isEmpty()) {
                                                    _searchedTags.add(
                                                        Tag(
                                                            tagname = event.tag, tagcount = 0
                                                        )
                                                    )
                                                } else {
                                                    _searchedTags.addAll(it.tags)
                                                }
                                            }
                                        }
                                    }

                                    is Resource.Error -> {
                                        setEvent(
                                            UiEvent.ShowToast(
                                                message = result.message ?: getString(
                                                    context,
                                                    R.string.error
                                                )
                                            )
                                        )
                                    }

                                    else -> null
                                }
                            }
                    }
                } else {
                    _searchedTags.clear()
                }
            }

            is UploadPostEvent.AddTag -> {
                _tagTextField.value = ""
                if (!_addedTags.contains(searchedTags[event.tagIndex].tagname))
                    _addedTags.add(searchedTags[event.tagIndex].tagname)
                _searchedTags.clear()
            }

            is UploadPostEvent.DeleteTag -> {
                _addedTags.remove(event.tag.replace("#", ""))
            }

            is UploadPostEvent.TypeContent -> {
                _contentTextField.value = event.text
            }

            is UploadPostEvent.ToggleCheckBox -> {
                _anonymous.value = event.isChecked
                val message =
                    if (event.isChecked) getString(context, R.string.anonymous_on) else getString(
                        context,
                        R.string.anonymous_off
                    )
                viewModelScope.launch {
                    setEvent(
                        UiEvent.ShowToast(
                            message = message
                        )
                    )
                }

            }

            is UploadPostEvent.AddImages -> {
                _selectedImages.addAll(
                    event.images.map { uri ->
                        EditableImage(uri = uri, isNew = true)
                    }
                )
            }

            is UploadPostEvent.DeleteImage -> {
                _selectedImages.remove(event.image)
                if (!event.image.isNew && event.image.remotePath != null) {
                    deletedImages.add(event.image.remotePath)
                }
                _selectedImages.remove(event.image)
            }

            is UploadPostEvent.SetLocationOnOff -> {
                _locationOnOff.value = event.onOff
            }

            is UploadPostEvent.ToggleLocationOnOff -> {
                if (postMode == PostMode.CREATE) {
                    val message = if (event.onOff) getString(
                        context,
                        R.string.location_on
                    ) else getString(context, R.string.location_off)
                    viewModelScope.launch {
                        setEvent(
                            UiEvent.ShowToast(
                                message = message
                            )
                        )
                        _locationOnOff.value = event.onOff
                    }
                } else {
                    viewModelScope.launch {
                        setEvent(
                            UiEvent.ShowToast(
                                getString(context, R.string.unable_to_change_location)
                            )
                        )

                    }
                }

            }

            is UploadPostEvent.UploadPost -> {
                handleUploadPost(event)
                /**viewModelScope.launch {
                    var requestTags: RequestBody? = null
                    var requestImages: List<MultipartBody.Part>? = null
                    if (addedTags.isNotEmpty())
                        requestTags = addedTags.joinToString("#")
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    var requestAudio: MultipartBody.Part? = null
                    val requestText =
                        contentTextField.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    var requestLat: MultipartBody.Part? = null
                    var requestLong: MultipartBody.Part? = null
                    event.lat?.let {
                        requestLat =
                            MultipartBody.Part.createFormData("latitude", event.lat.toString())
                        requestLong =
                            MultipartBody.Part.createFormData("longitude", event.long.toString())
                    }
                    if (selectedImages.isNotEmpty()) {
                        requestImages = selectedImages.filter { it.isNew }
                            .mapNotNull { item ->
                                item.uri?.let { getMultipartBody(it, context) }
                            }
                    }
                    event.audioFilePath?.let { filePath ->
                        val file = File(filePath)

                        if (file.exists()) {
                            val requestFile =
                                file.asRequestBody("audio/mp4".toMediaTypeOrNull())
                            requestAudio =
                                MultipartBody.Part.createFormData(
                                    "audio",
                                    file.name,
                                    requestFile
                                )

                        }
                    }
                    if (postMode == PostMode.CREATE) {
                        var requestVoteOptions: RequestBody? = null

                        if (event.voteOptions.isNotEmpty()) {
                            val voteOptionDataList = event.voteOptions.map {
                                VoteOptionData(voteoption = it)
                            }
                            val gson = Gson()
                            val voteOptionsJson = gson.toJson(voteOptionDataList)
                            requestVoteOptions =
                                voteOptionsJson.toRequestBody("application/json".toMediaType())
                        }

                        uploadPostUseCases.uploadPost(
                            anonymousNick = if (anonymous.value) generateAnonymousNickname().toRequestBody(
                                "text/plain".toMediaTypeOrNull()
                            ) else null,
                            tags = requestTags,
                            images = requestImages,
                            text = requestText,
                            audio = requestAudio,
                            voteOptions = requestVoteOptions,
                            latitude = requestLat,
                            longitude = requestLong
                        ).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    setLoading(false)
                                    setEvent(UiEvent.popBackStack)

                                }

                                is Resource.Error -> {
                                    setLoading(false)
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(
                                                context,
                                                R.string.error
                                            )
                                        )
                                    )
                                }

                                is Resource.Loading -> {
                                    setLoading(true)
                                }

                                else -> null
                            }
                        }
                    } else {
                        val deleteImagesJson = Gson().toJson(deletedImages)
                        val deleteImagesBody =
                            deleteImagesJson.toRequestBody("application/json".toMediaTypeOrNull())
                        uploadPostUseCases.editPost(
                            postid = MultipartBody.Part.createFormData("postid", postId.toString()),
                            latitude = requestLat,
                            longitude = requestLong,
                            anonymousNick = if (anonymous.value) generateAnonymousNickname().toRequestBody(
                                "text/plain".toMediaTypeOrNull()
                            ) else null,
                            deleteImages = deleteImagesBody,
                            tags = requestTags,
                            images = requestImages,
                            audio = requestAudio,
                            deleteAudio = event.deleteAudio
                                ?.let { it.toRequestBody("text/plain".toMediaTypeOrNull()) },
                            text = requestText,


                            ).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    setLoading(false)
                                    result.data?.let {
                                        setEvent(
                                            UiEvent.PopBackStackWithResult(
                                                getString(
                                                    context,
                                                    R.string.editedPost
                                                ), it.posts[0]
                                            )
                                        )
                                    }
                                }

                                is Resource.Error -> {
                                    setLoading(false)
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(
                                                context,
                                                R.string.error
                                            )
                                        )
                                    )
                                }

                                is Resource.Loading -> {
                                    setLoading(true)
                                }

                                else -> null
                            }

                        }
                    }
                }**/
            }

        }


    }

    private fun handleUploadPost(event: UploadPostEvent.UploadPost) {
        viewModelScope.launch {
            val requestBodies = buildRequestBodies(event)
            when (postMode) {
                PostMode.CREATE -> uploadNewPost(requestBodies, event)
                PostMode.EDIT -> editExistingPost(requestBodies, event)
                else -> null
            }

        }
    }

    private fun buildRequestBodies(event: UploadPostEvent.UploadPost): UploadRequestData {
        val requestTags = if (addedTags.isNotEmpty()) {
            addedTags.joinToString("#").toRequestBody("text/plain".toMediaTypeOrNull())
        } else null

        val requestText = contentTextField.value.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestImages = selectedImages.filter { it.isNew }
            .mapNotNull { it.uri?.let { uri -> getMultipartBody(uri, context) } }

        val requestAudio = event.audioFilePath?.let { path ->
            val file = File(path)
            if (file.exists()) MultipartBody.Part.createFormData(
                "audio", file.name, file.asRequestBody("audio/mp4".toMediaTypeOrNull())
            ) else null
        }

        val requestLat =
            event.lat?.let { MultipartBody.Part.createFormData("latitude", it.toString()) }
        val requestLong =
            event.long?.let { MultipartBody.Part.createFormData("longitude", it.toString()) }

        return UploadRequestData(
            tags = requestTags,
            text = requestText,
            images = requestImages,
            audio = requestAudio,
            latitude = requestLat,
            longitude = requestLong
        )
    }

    private suspend fun uploadNewPost(data: UploadRequestData,event: UploadPostEvent.UploadPost) {
        val voteOptionsJson = event.voteOptions.takeIf { it.isNotEmpty() }?.let {
            val json = Gson().toJson(it.map { VoteOptionData(voteoption = it) })
            json.toRequestBody("application/json".toMediaType())
        }

        uploadPostUseCases.uploadPost(
            anonymousNick = if (anonymous.value)
                generateAnonymousNickname().toRequestBody("text/plain".toMediaTypeOrNull())
            else null,
            tags = data.tags,
            images = data.images,
            text = data.text,
            audio = data.audio,
            voteOptions = voteOptionsJson,
            latitude = data.latitude,
            longitude = data.longitude
        ).collect { handleResult(it,PostMode.CREATE) }
    }

    private suspend fun editExistingPost(
        data: UploadRequestData,
        event: UploadPostEvent.UploadPost
    ) {
        val deleteImagesJson = Gson().toJson(deletedImages)
        val deleteImagesBody =
            deleteImagesJson.toRequestBody("application/json".toMediaTypeOrNull())

        uploadPostUseCases.editPost(
            postid = MultipartBody.Part.createFormData("postid", postId.toString()),
            latitude = data.latitude,
            longitude = data.longitude,
            anonymousNick = if (anonymous.value)
                generateAnonymousNickname().toRequestBody("text/plain".toMediaTypeOrNull())
            else null,
            deleteImages = deleteImagesBody,
            tags = data.tags,
            images = data.images,
            audio = data.audio,
            deleteAudio = event.deleteAudio?.toRequestBody("text/plain".toMediaTypeOrNull()),
            text = data.text
        ).collect { handleResult(it, PostMode.EDIT) }
    }

    private suspend fun <T> handleResult(
        result: Resource<T>,
        mode: PostMode
    ) {
        when (result) {
            is Resource.Success -> {
                setLoading(false)
                when (mode) {
                    PostMode.CREATE -> {
                        setEvent(UiEvent.popBackStack)
                    }
                    PostMode.EDIT -> {
                        // T가 어떤 타입이든 런타임에 확인 가능
                        if (result.data is GetPostsResponse) {
                            val data = result.data as GetPostsResponse
                            setEvent(
                                UiEvent.PopBackStackWithResult(
                                    getString(context, R.string.editedPost),
                                    data.posts.first()
                                )
                            )
                        }
                    }
                }
            }

            is Resource.Error -> {
                setLoading(false)
                setEvent(
                    UiEvent.ShowToast(
                        result.message ?: getString(context, R.string.error)
                    )
                )
            }

            is Resource.Loading -> setLoading(true)
        }
    }
}
data class UploadRequestData(
    val tags: RequestBody?,
    val text: RequestBody,
    val images: List<MultipartBody.Part>?,
    val audio: MultipartBody.Part?,
    val latitude: MultipartBody.Part?,
    val longitude: MultipartBody.Part?
)
data class VoteOptionData(
    val voteoption: String
)
enum class PostMode {
    CREATE,
    EDIT
}