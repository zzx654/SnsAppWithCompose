package com.androiddev.domain.use_case.user

import javax.inject.Inject

data class UserUseCases @Inject constructor(
    val getSearchedUsers:GetSearchedUsers,
    val toggleFollowUser: ToggleFollowUser,
    val getUserInfo: GetUserInfo,
    val getMediaPosts: GetMediaPosts
)