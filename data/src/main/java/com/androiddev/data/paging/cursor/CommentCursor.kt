package com.androiddev.data.paging.cursor

sealed interface CommentCursor {
    // 1. 일반 댓글/답글 커서 (생성일자 및 ID 기준)
    data class Default(
        val commentId: Int,
        val commentDate: String
    ) : CommentCursor

    data class Popular(
        val commentId: Int,
        val commentScore: Int
    )

    // 2. 알림/필터링용 커서 (필요시 특정 parentId나 targetId 등을 커서에 포함)
    data class NotificationFiltered(
        val commentId: Long,
        val targetCommentId: Long?, // 강조하거나 먼저 노출할 댓글 ID
        val createdAt: String
    ) : CommentCursor
}