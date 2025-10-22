package com.androiddev.data.remote.dto

import com.androiddev.domain.model.GetPostsResponse

data class GetPostsResponseDto(
    val posts:List<PostDto>,
    val isTokenValid:Boolean,
    val resultCode: Int
)
fun GetPostsResponseDto.toGetPostsResponse(
    posts:List<PostDto>,
    isTokenValid: Boolean): GetPostsResponse {
    return GetPostsResponse(
        posts = posts.map{
            it.toPostPreview(
                postid = it.postid,
                userid = it.userid,
                nickname = it.nickname,
                anonymous = it.anonymous,
                profileimage = it.profileimage,
                gender = it.gender,
                text = it.text,
                tags = it.tags,
                date = it.date,
                images = it.images,
                audio = it.audio,
                commentcount = it.commentcount,
                likecount = it.likecount,
                isliked = it.isliked,
                popularityScore = it.popularityScore,
                distance = it.distance,
                vote = it.vote,
                votecount = it.votecount

            ) },
        isTokenValid = isTokenValid
    )
}
