package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.user.UserApi
import com.androiddev.data.remote.dto.UsersDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.model.User
import retrofit2.Response


class UserStrategy (
    private val nickname:String,
    private val api: UserApi,
): PagingStrategy<UsersDto, User, Int> {
    override suspend fun fetch(cursor: Int?): Response<BaseApiResponse<UsersDto>> {
        return api.getSearchedUsers(
            nickname = nickname,
            lastuserid = cursor
        )
    }

    override fun mapToDomain(data: UsersDto): List<User> {

        return data.users.map{ it.toDomain() }
    }

    override fun extractNextCursor(items: List<User>, pageSize: Int): Int? {
        if (items.isEmpty() || items.size < pageSize) return null
        val lastItem = items.last()
        return lastItem.userId
    }


}