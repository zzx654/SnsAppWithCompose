package com.androiddev.snsappwithcompose.feature.upload_post

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.model.Tag
import com.androiddev.domain.use_case.uploadpost.UploadPostUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.generateAnonymousNickname
import com.androiddev.data.util.getMultipartBody
import com.androiddev.domain.model.MediaType
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.upload_post.component.EditableImage
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.util.getVideoThumbnail
import com.androiddev.snsappwithcompose.feature.upload_post.util.isVideo
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCases: UploadPostUseCases,
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
): BaseViewModel(context) {

    val args: Screen.UploadPostScreen = savedStateHandle.toRoute<Screen.UploadPostScreen>()
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
    private val _selectedMediaItems = mutableStateListOf<MediaItem>()
    val selectedMediaItems: SnapshotStateList<MediaItem>
        get() = _selectedMediaItems
    val deletedVisualMedia = mutableStateListOf<String>()
    private val _locationOnOff = mutableStateOf(false)
    val locationOnOff: State<Boolean>
        get() = _locationOnOff
    //var postId: Int? = null
    var isInitialized = false
        private set
    private val _postMode = MutableStateFlow(PostMode.CREATE)
    val postMode: StateFlow<PostMode>
        get() = _postMode
    private val _cachedVote:MutableStateFlow<String?> =  MutableStateFlow(null)
    val cachedVote = _cachedVote.asStateFlow()
    private val _cachedAudio:MutableStateFlow<String?> = MutableStateFlow(null)
    val cachedAudio:StateFlow<String?> = _cachedAudio.asStateFlow()

    private val _cachedText:MutableStateFlow<String> = MutableStateFlow("")
    val cachedText:StateFlow<String> = _cachedText.asStateFlow()

    init {
        if (postMode.value == PostMode.CREATE) {
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
        loadCachedPost()

    }
    private fun addMedia(context: Context,uriList: List<Uri>) {
        viewModelScope.launch {
            val newItems = uriList.map { uri ->
                if (isVideo(context, uri)) {
                    val thumbnail = withContext(Dispatchers.IO) {
                        getVideoThumbnail(context, uri)
                    }
                    MediaItem(
                        uri = uri,
                        type = MediaType.VIDEO,
                        thumbnail = thumbnail,
                        isNew = true
                    )
                } else {
                    MediaItem(
                        uri = uri,
                        type = MediaType.IMAGE
                    ,isNew = true)
                }
            }
            _selectedMediaItems.addAll(newItems)

        }
    }
    private fun loadCachedPost() {
        args.postId?.let {
            val cachedPost = postRepository.getCachedPost(it)
            if (cachedPost != null) {
                _postMode.value = PostMode.EDIT
                val tags = cachedPost.tags?.split('#')?.filter { it.isNotBlank() }
                tags?.let {
                    _addedTags.addAll(it)
                }
                _anonymous.value = cachedPost.anonymousNickname!=null
                _locationOnOff.value = cachedPost.location != null
                _cachedVote.value = cachedPost.vote
                _cachedText.value = cachedPost.text

                _cachedAudio.value = cachedPost.media.firstOrNull { it.type == MEDIA_TYPE_AUDIO }?.url
                val visualMedia = cachedPost.media.filter{ it.type == MEDIA_TYPE_IMAGE || it.type == MEDIA_TYPE_VIDEO }
                if(visualMedia.isNotEmpty()) {

                    _selectedMediaItems.clear()
                    _selectedMediaItems.addAll(
                        visualMedia.map { media ->
                            if (media.type == MEDIA_TYPE_VIDEO) {

                                MediaItem(
                                    type = MediaType.VIDEO,
                                    remotePath = media.url,
                                    remoteThumbnailPath = media.thumbnailUrl,
                                    isNew = false
                                )
                            } else {
                                MediaItem(
                                    type = MediaType.IMAGE,
                                    remotePath = media.url
                                    ,isNew = false)
                            }
                        }
                    )

                }

            }
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
                                                if (it.searchedTags.isEmpty()) {
                                                    _searchedTags.add(
                                                        Tag(
                                                            tagname = event.tag, tagcount = 0
                                                        )
                                                    )
                                                } else {
                                                    _searchedTags.addAll(it.searchedTags)
                                                }
                                            }
                                        }
                                    }

                                    is Resource.Error -> {
                                       // setEvent(
                                     //       UiEvent.ShowToast(
                                         //       message = result.message ?: getString(
                                                //    context,
                                                //    R.string.error
                                            //    )
                                        //    )
                                      //  )
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
                   // setEvent(
                  //      UiEvent.ShowToast(
                  //          message = message
                  //      )
                  //  )
                }

            }
            is UploadPostEvent.AddMedia -> {
                addMedia(context,event.uris)

            }

            is UploadPostEvent.AddImages -> {
                _selectedImages.addAll(
                    event.images.map { uri ->
                        EditableImage(uri = uri, isNew = true)
                    }
                )
            }

            is UploadPostEvent.SetLocationOnOff -> {
                _locationOnOff.value = event.onOff
            }

            is UploadPostEvent.ToggleLocationOnOff -> {
                if (postMode.value == PostMode.CREATE) {
                    val message = if (event.onOff) getString(
                        context,
                        R.string.location_on
                    ) else getString(context, R.string.location_off)
                    viewModelScope.launch {
                      //  setEvent(
                      //      UiEvent.ShowToast(
                      //          message = message
                      //      )
                     //   )
                        _locationOnOff.value = event.onOff
                    }
                } else {
                    viewModelScope.launch {
                        setEvent(
                            UiEvent.ShowToast(
                                UiText.StringResource(R.string.unable_to_change_location)
                            )
                        )

                    }
                }

            }

            is UploadPostEvent.UploadPost -> {
                handleUploadPost(event)
            }
            is UploadPostEvent.DeleteMedia -> {
                _selectedMediaItems.remove(event.media)
                if (!event.media.isNew && event.media.remotePath != null) {
                    deletedVisualMedia.add(event.media.remotePath)
                }
            }

            else -> {}
        }


    }

    private fun handleUploadPost(event: UploadPostEvent.UploadPost) {
        viewModelScope.launch {
            val requestBodies = buildRequestBodies(event)
            when (postMode.value) {
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
        val mediaParts = mutableListOf<MultipartBody.Part>()
        val mediaTypes = mutableListOf<RequestBody>()

        selectedMediaItems.filter { it.isNew }.forEach { media ->
            media.uri?.let { uri ->

                val part = getMultipartBody(
                    uri = uri,
                    context = context,
                    type = media.type
                )
                mediaParts.add(part)
                mediaTypes.add(media.type.name.toRequestBody("text/plain".toMediaTypeOrNull()))


            }

        }
        event.audioFilePath?.let { path ->
            val part = getMultipartBody(
                path = path,
                context = context,
                type = MediaType.AUDIO
            )
            mediaParts.add(part)
            mediaTypes.add(MediaType.AUDIO.name.toRequestBody("text/plain".toMediaTypeOrNull()))
        }


        val requestLat =
            event.lat?.let { MultipartBody.Part.createFormData("latitude", it.toString()) }
        val requestLong =
            event.long?.let { MultipartBody.Part.createFormData("longitude", it.toString()) }

        return UploadRequestData(
            tags = requestTags,
            text = requestText,
            media = if(mediaParts.isEmpty()) null else mediaParts,
            mediaTypes = if(mediaTypes.isEmpty()) null else mediaTypes,
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
            media = data.media,
            mediaTypes = data.mediaTypes,
            text = data.text,

            voteOptions = voteOptionsJson,
            latitude = data.latitude,
            longitude = data.longitude
        ).collect { handleResult(it) }
    }

    private suspend fun editExistingPost(
        data: UploadRequestData,
        event: UploadPostEvent.UploadPost
    ) {
        val deletedVisualMediaJson = Gson().toJson(deletedVisualMedia)
        val deletedVisualMediaBody =
            deletedVisualMediaJson.toRequestBody("application/json".toMediaTypeOrNull())
        args.postId?.let { postId ->
            uploadPostUseCases.editPost(
                postid = MultipartBody.Part.createFormData("postid", postId.toString()),
                latitude = data.latitude,
                longitude = data.longitude,
                anonymousNick = if (anonymous.value)
                    generateAnonymousNickname().toRequestBody("text/plain".toMediaTypeOrNull())
                else null,
                deletedVisualMedia = deletedVisualMediaBody,
                tags = data.tags,
                media = data.media,
                mediaTypes = data.mediaTypes,
                deletedAudio = event.deleteAudio?.toRequestBody("text/plain".toMediaTypeOrNull()),
                text = data.text
            ).collect { handleResult(it) }

        }


    }

    private suspend fun <T> handleResult(
        result: Resource<T>,
    ) {
        handleResource(
            resource = result,
            onSuccessUnit = {
                setEvent(UiEvent.popBackStack)
            },
            onSuccess = { data ->
                if (result.data is List<*>) {
                    val data = result.data as List<Post>
                    postRepository.updateCachedPost(data.first())
                    setEvent(
                        UiEvent.popBackStack
                    )
                }
            }
        )
    }
}
data class UploadRequestData(
    val tags: RequestBody?,
    val text: RequestBody,
    val media:List<MultipartBody.Part>?,
    val mediaTypes:List<RequestBody>?,
    //val images: List<MultipartBody.Part>?,
    //val audio: MultipartBody.Part?,
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