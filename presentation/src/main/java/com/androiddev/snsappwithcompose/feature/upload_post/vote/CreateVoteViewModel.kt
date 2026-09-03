package com.androiddev.snsappwithcompose.feature.upload_post.vote

import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseViewModel

import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.BottomSheetDialogState
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.upload_post.PostMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateVoteViewModel @Inject constructor(
) : BaseViewModel() {
    companion object {
        private const val MIN_VOTE_OPTION_COUNT = 2
        private const val DEFAULT_VOTE_OPTION_COUNT = 3
    }

    private val _uiState = MutableStateFlow(CreateVoteUiState())
    val uiState: StateFlow<CreateVoteUiState> = _uiState.asStateFlow()

    private val _manageVoteDialogState = MutableStateFlow(BottomSheetDialogState<VoteOption>())
    val manageVoteDialogState: StateFlow<BottomSheetDialogState<VoteOption>> = _manageVoteDialogState.asStateFlow()

    fun initVoteState() {
        _uiState.update { it.copy(saved = true) }
    }

    fun onEvent(event: CreateVoteEvent) {
        when (event) {
            is CreateVoteEvent.OnAddVoteClick -> {
                if (event.postMode == PostMode.CREATE) {
                    if (_uiState.value.saved) {
                        showManageVoteDialog()
                    } else {
                        _uiState.update { it.copy(showBottomVoteDialog = true) }
                    }
                } else {
                    viewModelScope.launch {
                        setEvent(
                            UiEvent.ShowToast(
                                message = UiText.StringResource(R.string.cannot_edit_vote)
                            )
                        )
                    }
                }
            }
            is CreateVoteEvent.OnAddVoteOptionClick -> addVoteOption()
            is CreateVoteEvent.onCancelClick -> {
                _uiState.update { currentState ->
                    val saved = currentState.savedVoteOptions
                    // 저장되어 있던 데이터가 3개 미만이면 빈칸을 채워서 3개로 복원
                    val restoredOptions = if (saved.size < DEFAULT_VOTE_OPTION_COUNT) {
                        saved + List(DEFAULT_VOTE_OPTION_COUNT - saved.size) { "" }
                    } else {
                        saved
                    }

                    currentState.copy(
                        voteOptions = restoredOptions,
                        showBottomVoteDialog = false
                    )
                }
            }
            is CreateVoteEvent.TypeVoteOption -> {
                updateVoteOption(index = event.index, newValue = event.option)
            }
            is CreateVoteEvent.SaveVoteOptions -> {
                if (!saveVoteOptionsToMemory()) {
                    viewModelScope.launch {
                        setEvent(
                            UiEvent.ShowToast(
                                message = UiText.StringResource(R.string.error_minimum_poll_options)
                            )
                        )
                    }
                }
            }
            else -> Unit
        }
    }

    private fun addVoteOption() {
        _uiState.update {
            it.copy(voteOptions = it.voteOptions + "")
        }
    }

    private fun updateVoteOption(index: Int, newValue: String) {
        _uiState.update { currentState ->
            val updatedList = currentState.voteOptions.toMutableList().apply {
                if (index in indices) {
                    this[index] = newValue
                }
            }
            currentState.copy(voteOptions = updatedList)
        }
    }

    private fun saveVoteOptionsToMemory(): Boolean {
        //  실제 내용이 입력된 텍스트만 추출 (저장용)
        val cleanedList = _uiState.value.voteOptions
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val isValidVoteCount = cleanedList.size >= MIN_VOTE_OPTION_COUNT

        return if (isValidVoteCount) {
            // 화면의 입력 필드(voteOptions)는 유저가 다시 열었을 때 최소 3개의 칸이 유지되도록 처리

            val formattedOptionsForInput = if (cleanedList.size < DEFAULT_VOTE_OPTION_COUNT) {
                cleanedList + List(DEFAULT_VOTE_OPTION_COUNT - cleanedList.size) { "" }
            } else {
                cleanedList
            }

            _uiState.update {
                it.copy(
                    voteOptions = formattedOptionsForInput, // 입력창 필드는 최소 3개 유지
                    savedVoteOptions = cleanedList,          // 실제 저장 데이터는 알맹이만
                    saved = true,
                    showBottomVoteDialog = false
                )
            }
            true
        } else {
            false
        }
    }

    private fun deleteVoteOptions() {
        _uiState.update {
            it.copy(
                voteOptions = List(3) { "" },
                savedVoteOptions = emptyList(),
                saved = false
            )
        }
    }

    private fun showManageVoteDialog() {
        val options = listOf(VoteOption.Edit, VoteOption.Delete)

        _manageVoteDialogState.value = BottomSheetDialogState(
            showDialog = true,
            options = options,
            onOptionSelected = { option ->
                resetManageVoteDialogState()
                handleVoteOption(option)
            },
            onClickCancel = { resetManageVoteDialogState() }
        )
    }

    private fun handleVoteOption(option: VoteOption) {
        when (option) {
            VoteOption.Edit -> {
                _uiState.update { it.copy(showBottomVoteDialog = true) }
            }
            VoteOption.Delete -> {
                deleteVoteOptions()
            }
        }
    }

    private fun resetManageVoteDialogState() {
        _manageVoteDialogState.value = BottomSheetDialogState()
    }
}
