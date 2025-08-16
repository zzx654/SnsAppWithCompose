package com.androiddev.data.remote.dto

import com.androiddev.domain.model.GetCommentsResponse

data class GetCommentsResponseDto(
    val comments:List<CommentDto>,
    val isTokenValid:Boolean,
    val resultCode: Int
)
fun GetCommentsResponseDto.toGetCommentsResponse(
    comments:List<CommentDto>,
    isTokenValid: Boolean): GetCommentsResponse {
    return GetCommentsResponse(
        comments = comments.map{
            it.toComment(
                postid = it.postid,
                commentid = it.commentid,
                userid = it.userid,
                text = it.text,
                ref = it.ref,
                date = it.date,
                depth = it.depth,
                anonymous = it.anonymous,
                nickname = it.nickname,
                gender = it.gender,
                profileimage = it.profileimage,
                replycount = it.replycount,
                likecount = it.likecount,
                commentliked = it.commentliked,
                score = it.score
            ) },
        isTokenValid = isTokenValid
    )
}