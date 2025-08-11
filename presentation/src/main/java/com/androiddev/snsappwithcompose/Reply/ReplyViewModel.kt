package com.androiddev.snsappwithcompose.Reply

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.androiddev.domain.use_case.CommentUseCases
import com.androiddev.snsappwithcompose.PostDetail.CommentEvent
import com.androiddev.snsappwithcompose.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val commentUseCases: CommentUseCases,
    private val context: Context
): BaseViewModel() {
    val _commentText = mutableStateOf("")
    val commentText: State<String>
        get() = _commentText
    val _anonymousChecked = mutableStateOf(false)
    val anonymousChecked: State<Boolean>
        get() = _anonymousChecked
    fun onEvent(event: CommentEvent) {


    }
}