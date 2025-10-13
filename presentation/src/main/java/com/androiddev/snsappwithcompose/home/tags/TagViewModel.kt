package com.androiddev.snsappwithcompose.home.tags

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Tag
import com.androiddev.domain.use_case.TagUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val context: Context,
    private val tagUseCases: TagUseCases
): BaseViewModel() {
    private val _tagTextField = mutableStateOf("")
    val tagTextField: State<String>
        get() = _tagTextField
    protected val _getTagsState = mutableStateOf(GetTagsState())
    val getTagsState: State<GetTagsState> get() = _getTagsState
    init {
        viewModelScope.launch {
            tagUseCases.getTags()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            setLoading(false)
                            result.data?.let {
                                _getTagsState.value = getTagsState.value.copy(
                                    isLoading = false,
                                    favoriteTags = it.favoriteTags,
                                    popularTags = it.popularTags
                                )


                            }
                        }
                        is Resource.Error -> {
                            setLoading(false)
                        }
                        is Resource.Loading -> {
                            setLoading(true)
                        }
                    }

                }
        }
    }
    fun onEvent(event: TagEvent) {
        when(event) {
            is TagEvent.TypeTag -> {
                _tagTextField.value = event.tag
                if(event.tag.isNotEmpty()) {
                    viewModelScope.launch {
                        delay(50L)
                        tagUseCases.searchTag(event.tag)
                            .collect { result ->
                                when(result) {
                                    is Resource.Success -> {
                                        result.data?.let {
                                            if(tagTextField.value.isNotEmpty()) {
                                                _getTagsState.value = getTagsState.value.copy(
                                                    searchedTags = it.tags
                                                )


                                            }
                                        }
                                    }
                                    is Resource.Error -> {
                                        setEvent(
                                            UiEvent.ShowToast(
                                                message = result.message ?: getString(context, R.string.error)
                                            )
                                        )
                                    }
                                    else -> null
                                }
                            }
                    }
                } else {
                    _getTagsState.value = getTagsState.value.copy(
                        searchedTags = listOf()
                    )
                }
            }
        }

    }
}