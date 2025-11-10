package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Posts

data class GetPostsDto (
    val posts:List<PostDto>
)
fun GetPostsDto.toPosts(
    posts:List<PostDto>
):Posts {
    return Posts(
        posts = posts.map{
            it.toPostPreview(
                postid = it.postid,
                userid = it.userid,
                nickname = it.nickname,
                anonymous = it.anonymous,
                location = it.location,
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

            ) }
    )
}