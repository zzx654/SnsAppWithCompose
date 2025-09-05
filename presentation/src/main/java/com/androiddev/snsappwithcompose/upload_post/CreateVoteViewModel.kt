package com.androiddev.snsappwithcompose.upload_post

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.BottomSheetItem
import com.androiddev.snsappwithcompose.util.CustomBottomSheetDialogState
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateVoteViewModel @Inject constructor(
    val context: Context
): BaseViewModel() {

    private val _manageVoteDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val manageVoteDialogState: State<CustomBottomSheetDialogState>
        get() = _manageVoteDialogState
    private val _showBottomVoteDialog: MutableState<Boolean> = mutableStateOf(false)
    val showBottomVoteDialog: State<Boolean>
        get() = _showBottomVoteDialog

    // 사용자가 입력 중인 값
    var voteOptions by mutableStateOf(List(3) { "" })
        private set

    // 체크 버튼 눌렀을 때 저장된 값
    var savedVoteOptions by mutableStateOf<List<String>>(emptyList())
        private set

    fun onEvent(event: CreateVoteEvent) {
        when(event) {
            is CreateVoteEvent.OnAddVoteClick -> {
                if(savedVoteOptions.isEmpty() )
                    _showBottomVoteDialog.value = true
                else
                    showManageVoteDialog()


            }
            is CreateVoteEvent.OnAddVoteOptionClick -> {
                addVoteOption()
            }
            is CreateVoteEvent.onCancelClick -> {
                voteOptions = savedVoteOptions
                _showBottomVoteDialog.value = false

            }
            is CreateVoteEvent.TypeVoteOption -> {
                updateVoteOption(index = event.index, newValue = event.option)
            }
            is CreateVoteEvent.SaveVoteOptions -> {
                if(saveVoteOptionsToMemory()) {
                    _showBottomVoteDialog.value = false
                } else {
                    viewModelScope.launch {
                        setEvent(
                            UiEvent.ShowToast(
                                message = getString(context,R.string.error_minimum_poll_options)
                            )
                        )
                    }

                }

            }
            else -> null
        }

    }
    // 보기 추가
    private fun addVoteOption() {
        voteOptions = voteOptions + ""
    }

    // 보기 내용 변경
    private fun updateVoteOption(index: Int, newValue: String) {
        voteOptions = voteOptions.toMutableList().also {
            it[index] = newValue
        }
    }

    private fun saveVoteOptionsToMemory(): Boolean {
        // 빈 항목 제거
        val cleanedList = voteOptions.map { it.trim() }.filter { it.isNotEmpty() }

        return if (cleanedList.size >= 2) {
            savedVoteOptions = cleanedList
            true
        } else {
            false
        }
    }
    private fun deleteVoteOptions() {
        voteOptions = List(3) { "" }
        savedVoteOptions = emptyList()
    }
    private fun showManageVoteDialog() {

        val items: MutableList<BottomSheetItem> = mutableListOf(
            BottomSheetItem(R.drawable.outline_edit,getString(context,R.string.option_modify)) {
                resetManageVoteDialogState()
                _showBottomVoteDialog.value = true
            },
            BottomSheetItem(R.drawable.delete,getString(context,R.string.option_delete)){
                resetManageVoteDialogState()
                deleteVoteOptions()

            }

        )
        _manageVoteDialogState.value = CustomBottomSheetDialogState(
            showDialog = true,
            items,
        ) { resetManageVoteDialogState() }



        _manageVoteDialogState.value = CustomBottomSheetDialogState(
            showDialog = true,
            items,
        ) { resetManageVoteDialogState() }
    }
    private fun resetManageVoteDialogState() {
        _manageVoteDialogState.value = CustomBottomSheetDialogState()
    }
}
