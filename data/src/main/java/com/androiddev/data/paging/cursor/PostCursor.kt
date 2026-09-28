package com.androiddev.data.paging.cursor

sealed interface PostCursor {

    data class Recent(
        val postId: Int,
        val postDate: String
    ) : PostCursor

    data class Popular(
        val postId: Int,
        val postScore: Double
    )
}