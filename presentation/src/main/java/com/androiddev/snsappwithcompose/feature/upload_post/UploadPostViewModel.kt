package com.androiddev.snsappwithcompose.feature.upload_post

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.androiddev.domain.model.Tag
import com.androiddev.domain.use_case.uploadpost.UploadPostUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.domain.model.MediaType
import com.androiddev.domain.model.UploadPostParam
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.component.toParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCases: UploadPostUseCases,
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle,
): BaseViewModel() {

    val args: Screen.UploadPostScreen = savedStateHandle.toRoute<Screen.UploadPostScreen>()

    private val _searchTagUiState = MutableStateFlow(SearchTagUiState())
    val searchTagUiState: StateFlow<SearchTagUiState> = _searchTagUiState.asStateFlow()

    private val _uiState = MutableStateFlow(UploadPostUiState())
    val uiState: StateFlow<UploadPostUiState> = _uiState.asStateFlow()

    private val _cachedVote: MutableStateFlow<String?> = MutableStateFlow(null)
    val cachedVote = _cachedVote.asStateFlow()

    private val _cachedAudio: MutableStateFlow<String?> = MutableStateFlow(null)
    val cachedAudio: StateFlow<String?> = _cachedAudio.asStateFlow()

    private val _cachedText: MutableStateFlow<String> = MutableStateFlow("")
    val cachedText: StateFlow<String> = _cachedText.asStateFlow()

    private val deletedVisualMedia = mutableStateListOf<String>()

    private val _postMode = MutableStateFlow(PostMode.CREATE)

    val postMode: StateFlow<PostMode>
        get() = _postMode.asStateFlow()

    init {
        loadCachedPost()
    }

    fun setLocationOnOff(isLocationOn: Boolean) {
        _uiState.update { it.copy(isLocationOn = isLocationOn) }
    }

    private fun loadCachedPost() {
        args.postId?.let { it ->
            val cachedPost = postRepository.getCachedPost(it)
            if (cachedPost != null) {
                _postMode.value = PostMode.EDIT
                val tags = cachedPost.tags?.split('#')?.filter { it.isNotBlank() }
                tags?.let { cachedTags ->
                    _searchTagUiState.update { currentState ->
                        currentState.copy(addedTags = currentState.addedTags + cachedTags)
                    }
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        isAnonymous = cachedPost.anonymousNickname != null,
                        isLocationOn = cachedPost.location != null
                    )
                }
                _cachedVote.value = cachedPost.vote
                _cachedText.value = cachedPost.text
                _cachedAudio.value =
                    cachedPost.media.firstOrNull { it.type == MEDIA_TYPE_AUDIO }?.url

                val visualMedia =
                    cachedPost.media.filter { it.type == MEDIA_TYPE_IMAGE || it.type == MEDIA_TYPE_VIDEO }
                if (visualMedia.isNotEmpty()) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedMediaItems = visualMedia.map { media ->
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
                                        remotePath = media.url, isNew = false
                                    )
                                }
                            }

                        )

                    }

                }

            }
        }
    }


    fun onEvent(event: UploadPostEvent) {
        when (event) {
            is UploadPostEvent.TypeTag -> {
                _searchTagUiState.update {
                    it.copy(tagText = event.tag)
                }
                if (event.tag.isNotEmpty()) {
                    viewModelScope.launch {
                        delay(50L)
                        uploadPostUseCases.searchTag(event.tag)
                            .collect { result ->
                                result.handle(
                                    onLoading = {
                                        _searchTagUiState.update {
                                            it.copy(isLoading = true)
                                        }
                                    },
                                    onSuccess = { data ->
                                        _searchTagUiState.update { currentState ->
                                            // 입력창이 비어있다면 결과 반영 없이 리턴 (또는 상태 유지)
                                            if (currentState.tagText.isEmpty()) return@update currentState

                                            // 검색 결과가 없으면 사용자 입력 태그(count = 0) 생성, 있으면 서버 데이터 사용
                                            val newSearchedTags = data.searchedTags.ifEmpty {
                                                listOf(Tag(tagname = event.tag, tagcount = 0))
                                            }

                                            currentState.copy(searchedTags = newSearchedTags)
                                        }
                                    },
                                    onFinally = {
                                        _searchTagUiState.update {
                                            it.copy(isLoading = false)
                                        }

                                    }
                                )

                            }
                    }
                } else {
                    _searchTagUiState.update {
                        it.copy(
                            searchedTags = emptyList()
                        )
                    }
                }
            }


            is UploadPostEvent.AddTag -> {
                _searchTagUiState.update { currentState ->
                    val selectedTag = currentState.searchedTags[event.tagIndex].tagname
                    val updatedTags = if (selectedTag in currentState.addedTags) {
                        currentState.addedTags
                    } else {
                        currentState.addedTags + selectedTag
                    }

                    currentState.copy(
                        tagText = "",
                        searchedTags = emptyList(),
                        addedTags = updatedTags
                    )
                }
            }

            is UploadPostEvent.DeleteTag -> {
                _searchTagUiState.update { currentState ->
                    val targetTag = event.tag.replace("#", "")
                    currentState.copy(
                        addedTags = currentState.addedTags - targetTag
                    )
                }
            }

            is UploadPostEvent.TypeContent -> {
                _uiState.update {
                    it.copy(
                        contentText = event.text
                    )
                }
            }

            is UploadPostEvent.ToggleCheckBox -> {
                _uiState.update {
                    it.copy(isAnonymous = event.isChecked)
                }
                val messageResource =
                    if (event.isChecked) R.string.anonymous_on else R.string.anonymous_off

                viewModelScope.launch {

                    setEvent(UiEvent.ShowToast(UiText.StringResource(messageResource)))

                }

            }

            is UploadPostEvent.AddMedia -> {
                _uiState.update {
                    it.copy(
                        selectedMediaItems = it.selectedMediaItems + event.items
                    )
                }
            }


            is UploadPostEvent.ToggleLocationOnOff -> {
                if (postMode.value == PostMode.CREATE) {
                    val messageResource =
                        if (uiState.value.isLocationOn) R.string.location_off else R.string.location_on
                    viewModelScope.launch {
                        setEvent(UiEvent.ShowToast(UiText.StringResource(messageResource)))
                    }
                    _uiState.update {
                        it.copy(
                            isLocationOn = !it.isLocationOn
                        )
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
                viewModelScope.launch {
                    setLoading(true)

                    val param = UploadPostParam(
                        postId = args.postId,
                        text = uiState.value.contentText,
                        tags = searchTagUiState.value.addedTags,
                        mediaItems = uiState.value.selectedMediaItems.map { it.toParam() },
                        audioPath = event.audioFilePath,
                        isAnonymous = uiState.value.isAnonymous,
                        isLocationEnabled = uiState.value.isLocationOn,
                        voteOptions = event.voteOptions
                    )
                    when (postMode.value) {
                        PostMode.CREATE -> {
                            uploadPostUseCases.uploadPost(
                                param = param
                            ).collect { result ->
                                result.handle (
                                    onSuccessUnit = {
                                        setEvent(
                                            UiEvent.popBackStack
                                        )
                                    }
                                )
                            }
                        }
                        PostMode.EDIT -> {
                            args.postId?.let { postId ->
                                uploadPostUseCases.editPost(
                                    postid = args.postId,
                                    deletedVisualMedia = deletedVisualMedia,
                                    deletedAudio = event.deletedAudio,
                                    param = param,
                                ).collect { result ->
                                    result.handle (
                                        onSuccess = { data ->
                                            val editedPost = data[0]
                                            postRepository.updateCachedPost(editedPost)
                                            setEvent(
                                                UiEvent.popBackStack
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is UploadPostEvent.DeleteMedia -> {
                _uiState.update {
                    it.copy(
                        selectedMediaItems = it.selectedMediaItems - event.media
                    )
                }
                if (!event.media.isNew && event.media.remotePath != null) {
                    deletedVisualMedia.add(event.media.remotePath)
                }
            }
        }
    }
}

enum class PostMode {
    CREATE,
    EDIT
}