package com.androiddev.data.paging.cursor

sealed interface PostCursor {

    data class Recent(
        val postId: Long,
        val postDate: String
    ) : PostCursor

    data class Popular(
        val postId: Long,
        val postScore: Double
    )
}