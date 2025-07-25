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
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.use_case.UploadPostUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.generateAnonymousNickname
import com.androiddev.snsappwithcompose.util.getMultipartBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCases: UploadPostUseCases,
    private val context: Context
): BaseViewModel() {
    private val _tagTextField = mutableStateOf("")
    val tagTextField: State<String>
        get() = _tagTextField
    private val _searchedTags = mutableStateListOf<TagInfo>()
    val searchedTags: SnapshotStateList<TagInfo>
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
    private val _selectedImages = mutableStateListOf<Uri>()
    val selectedImages: SnapshotStateList<Uri>
        get() = _selectedImages
    private val _locationOnOff = mutableStateOf(false)
    val locationOnOff: State<Boolean>
        get() = _locationOnOff
    init {
        checkPermissions(
            context = context,
            permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            onGranted = { _locationOnOff.value = true },
            onUnGranted = { _locationOnOff.value = false }
        )
    }
    fun onEvent(event: UploadPostEvent) {
        when(event) {
            is UploadPostEvent.TypeTag -> {
                _tagTextField.value = event.tag
                if(event.tag.isNotEmpty()) {
                    viewModelScope.launch {
                        delay(50L)
                        uploadPostUseCases.searchTag(event.tag)
                            .collect { result ->
                                when(result) {
                                    is Resource.Success -> {
                                        result.data?.let {
                                            if(tagTextField.value.isNotEmpty()) {
                                                _searchedTags.clear()
                                                if(it.isEmpty()) {
                                                    _searchedTags.add(TagInfo(event.tag.replace("#",""),0))
                                                } else {
                                                    _searchedTags.addAll(it)
                                                }
                                            }
                                        }
                                    }
                                    is Resource.Error -> {
                                        setEvent(
                                            UiEvent.ShowToast(
                                                message = result.message ?: getString(context,R.string.error)
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
                if(!_addedTags.contains(searchedTags[event.tagIndex].tagname))
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
                val message = if(event.isChecked) getString(context,R.string.anonymous_on) else getString(context,R.string.anonymous_off)
                viewModelScope.launch {
                    setEvent(
                        UiEvent.ShowToast(
                            message = message
                        )
                    )
                }

            }
            is UploadPostEvent.AddImages -> {
                _selectedImages.addAll(event.images)
            }
            is UploadPostEvent.DeleteImage -> {
                _selectedImages.remove(event.image)
            }
            is UploadPostEvent.SetLocationOnOff -> {
                _locationOnOff.value = event.onOff
            }
            is UploadPostEvent.ToggleLocationOnOff -> {
                val message = if(event.onOff) getString(context,R.string.location_on) else getString(context,R.string.location_off)
                viewModelScope.launch {
                    setEvent(
                        UiEvent.ShowToast(
                            message = message
                        )
                    )
                    _locationOnOff.value = event.onOff
                }
            }
            is UploadPostEvent.UploadPost -> {
                viewModelScope.launch {

                    var requestTags: RequestBody? = null
                    if(addedTags.isNotEmpty())
                        requestTags = addedTags.joinToString("#").toRequestBody("text/plain".toMediaTypeOrNull())

                    var requestImages: List<MultipartBody.Part>? = null
                    val requestText = contentTextField.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    var requestLat: MultipartBody.Part? = null
                    var requestLong: MultipartBody.Part? =  null

                    if(selectedImages.isNotEmpty()) {
                        requestImages = selectedImages.map{ getMultipartBody(it,context)}
                    }
                    event.lat?.let {
                        requestLat = MultipartBody.Part.createFormData("latitude",event.lat.toString())
                        requestLong = MultipartBody.Part.createFormData("longitude",event.long.toString())
                    }
                    uploadPostUseCases.uploadPost(
                        anonymousNick = if(anonymous.value) generateAnonymousNickname().toRequestBody("text/plain".toMediaTypeOrNull()) else null,
                        tags = requestTags,
                        images = requestImages,
                        text = requestText,
                        latitude = requestLat,
                        longitude = requestLong
                    ).collect { result ->
                            when(result) {
                                is Resource.Success -> {

                                }
                                is Resource.Error -> {
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context,R.string.error)
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
            else -> null
        }
    }
}