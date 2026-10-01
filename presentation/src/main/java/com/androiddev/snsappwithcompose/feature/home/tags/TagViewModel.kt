package com.androiddev.snsappwithcompose.feature.home.tags

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Tag
import com.androiddev.domain.use_case.tag.TagUseCases
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
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
                result.handle(
                    onSuccess = { data ->
                        _getTagsState.value = _getTagsState.value.copy(
                            isLoading = false,
                            favoriteTags = data.favoriteTags,
                            popularTags = data.popularTags
                        )

                    }
                )
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
                result.handle(
                    onSuccess = { result ->
                        if (tagTextField.value.isNotBlank()) {
                            _getTagsState.value = _getTagsState.value.copy(
                                searchedTags = result.searchedTags
                            )
                        }
                    }
                )
            }
        }
    }
    private fun toggleFavorite(tagId: Int) {
        viewModelScope.launch {
            tagUseCases.toggleFavoriteTag(tagId).collect { result ->
                result.handle(
                    onSuccess = { data ->
                        _getTagsState.value = _getTagsState.value.copy(
                            favoriteTags = data.favoriteTags,
                            popularTags = data.popularTags,
                            searchedTags = _getTagsState.value.searchedTags.map { tag ->
                                if (tag.tagid == tagId) tag.copy(isliked = if(tag.isliked==1) 0 else 1) else tag
                            }
                        )
                    }
                )
            }
        }
    }
    fun getTagById(tagId: Int): Tag? {
        return _getTagsState.value.favoriteTags.find { it.tagid == tagId }
            ?: _getTagsState.value.popularTags.find { it.tagid == tagId }
            ?: _getTagsState.value.searchedTags.find { it.tagid == tagId }
    }
}