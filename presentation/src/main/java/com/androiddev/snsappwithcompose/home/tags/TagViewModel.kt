package com.androiddev.snsappwithcompose.home.tags

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.TagUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
            }
        }

    }
}