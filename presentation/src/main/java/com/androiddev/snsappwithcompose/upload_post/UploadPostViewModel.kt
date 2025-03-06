package com.androiddev.snsappwithcompose.upload_post

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.use_case.UploadPostUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostUseCases: UploadPostUseCases,
    private val context: Context
): BaseViewModel() {
    private val _tagTextField = mutableStateOf("")
    val tagTextField: State<String>
        get() = _tagTextField
    private val _searchedTags = mutableStateOf(listOf<TagInfo>())
    val searchedTags: State<List<TagInfo>>
        get() = _searchedTags
    private val _addedTags = mutableStateOf(mutableSetOf<String>())
    val addedTags: State<MutableSet<String>>
        get() = _addedTags
    private val _contentTextField = mutableStateOf("")
    val contentTextField: State<String>
        get() = _contentTextField
    private val _anonymous = mutableStateOf(false)
    val anonymous: State<Boolean>
        get() = _anonymous
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
                                            if(tagTextField.value.isNotEmpty())
                                            _searchedTags.value = it
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
                    _searchedTags.value = listOf()
                }
            }
            is UploadPostEvent.AddTag -> {
                _tagTextField.value = ""
                _addedTags.value.add(searchedTags.value[event.tagIndex].tagname)
                _searchedTags.value = listOf()
            }
            is UploadPostEvent.DeleteTag -> {
                _addedTags.value.remove(event.tag)
            }
            is UploadPostEvent.TypeContent -> {
                _contentTextField.value = event.text
            }
            is UploadPostEvent.ToggleCheckBox -> {
                _anonymous.value = event.isChecked
            }
            else -> null
        }
    }
}