package com.androiddev.snsappwithcompose.home.tags


sealed class TagEvent {
    data class TypeTag(val tag: String) : TagEvent()

}