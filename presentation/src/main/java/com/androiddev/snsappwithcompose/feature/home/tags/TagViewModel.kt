package com.androiddev.snsappwithcompose.feature.home.tags

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Tag
import com.androiddev.domain.use_case.tag.TagUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.state.UiEvent
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
        fetchTags()
    }
    fun onEvent(event: TagEvent) {
        when (event) {
            is TagEvent.TypeTag -> {
                _tagTextField.value = event.tag
                searchTags(event.tag)
            }

            is TagEvent.ToggleFavoriteTag -> {
                toggleFavorite(event.tagId)
            }
        }
    }
    private fun fetchTags() {
        viewModelScope.launch {
            tagUseCases.getTags().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        setLoading(false)
                        result.data?.let {
                            _getTagsState.value = _getTagsState.value.copy(
                                isLoading = false,
                                favoriteTags = it.favoriteTags,
                                popularTags = it.popularTags
                            )
                        }
                    }
                    is Resource.Error -> setLoading(false)
                    is Resource.Loading -> setLoading(true)
                }
            }
        }
    }
    private fun searchTags(query: String) {
        if (query.isBlank()) {
            _getTagsState.value = _getTagsState.value.copy(searchedTags = emptyList())
            return
        }

        viewModelScope.launch {
            delay(50L)
            tagUseCases.searchTag(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            if (tagTextField.value.isNotBlank()) {
                                _getTagsState.value = _getTagsState.value.copy(
                                    searchedTags = it.tags
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        setEvent(
                            UiEvent.ShowToast(
                                result.message ?: getString(context, R.string.error)
                            )
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun toggleFavorite(tagId: Int) {
        viewModelScope.launch {
            tagUseCases.toggleFavoriteTag(tagId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        setLoading(false)
                        result.data?.let {
                            _getTagsState.value = _getTagsState.value.copy(
                                favoriteTags = it.favoriteTags,
                                popularTags = it.popularTags,
                                searchedTags = _getTagsState.value.searchedTags.map { tag ->
                                    if (tag.tagid == tagId) tag.copy(isliked = if(tag.isliked==1) 0 else 1) else tag
                                }
                            )
                        }
                    }
                    is Resource.Error -> setLoading(false)
                    is Resource.Loading -> setLoading(true)
                }
            }
        }
    }
    fun getTagById(tagId: Int): Tag? {
        return _getTagsState.value.favoriteTags.find { it.tagid == tagId }
            ?: _getTagsState.value.popularTags.find { it.tagid == tagId }
            ?: _getTagsState.value.searchedTags.find { it.tagid == tagId }
    }
}