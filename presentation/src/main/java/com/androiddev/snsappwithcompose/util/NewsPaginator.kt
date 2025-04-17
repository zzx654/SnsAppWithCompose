package com.androiddev.snsappwithcompose.util

interface Paginator<Key,Item> {
    suspend fun loadNextItems()
    fun reset()
}