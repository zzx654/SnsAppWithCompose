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
    var postId:Int? = null
    var isInitialized = false
        private set

    init {
        if(postMode == PostMode.CREATE) {
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

    fun initPost(post:PostPreview) {
        if(!isInitialized) {
            postMode = PostMode.EDIT
            postId = post.postId
            //여기서 이제 값들 나열
            //위치는 상관없이 그냥 누르면 토스트메시지로 못바꾼다고 알려주기
            post.tags?.let{
                _addedTags.addAll(it)
            }
            _anonymous.value = post.anonymous
            _locationOnOff.value = post.location != null
            //_contentTextField.value = post.text
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
                                                            tagname = event.tag.replace(
                                                                "#",
                                                                ""
                                                            ), tagcount = 0
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
                _addedTags.remove(event.tag)
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
                if(postMode==PostMode.CREATE) {
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
                               "저장된 위치를 변경할 수 없습니다"
                            )
                        )

                    }
                }

            }

            is UploadPostEvent.UploadPost -> {
                viewModelScope.launch {

                    var requestTags: RequestBody? = null
                    var requestVoteOptions: RequestBody? = null
                    if (addedTags.isNotEmpty())
                        requestTags = addedTags.joinToString("#")
                            .toRequestBody("text/plain".toMediaTypeOrNull())

                    var requestImages: List<MultipartBody.Part>? = null
                    var requestAudio: MultipartBody.Part? = null
                    val requestText =
                        contentTextField.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    var requestLat: MultipartBody.Part? = null
                    var requestLong: MultipartBody.Part? = null

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
                    if (event.voteOptions.isNotEmpty()) {
                        val voteOptionDataList = event.voteOptions.map {
                            VoteOptionData(voteoption = it)
                        }
                        val gson = Gson()
                        val voteOptionsJson = gson.toJson(voteOptionDataList)
                        requestVoteOptions =
                            voteOptionsJson.toRequestBody("application/json".toMediaType())
                    }
                    event.lat?.let {
                        requestLat =
                            MultipartBody.Part.createFormData("latitude", event.lat.toString())
                        requestLong = MultipartBody.Part.createFormData("longitude", event.long.toString())
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
                }
            }

        }


    }
}
data class VoteOptionData(
    val voteoption: String
)
enum class PostMode {
    CREATE,
    EDIT
}