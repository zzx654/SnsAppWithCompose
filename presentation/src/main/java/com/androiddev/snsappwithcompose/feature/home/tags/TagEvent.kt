package com.androiddev.snsappwithcompose.feature.home.tags


sealed class TagEvent {
    data class TypeTag(val tag: String) : TagEvent()
    data class ToggleFavoriteTag(val tagId: Int): TagEvent()

}