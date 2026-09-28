package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.user.UserApi
import com.androiddev.data.remote.dto.MediaPostsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.MediaPost
import retrofit2.Response


class MediaPostStrategy (
    private val api: UserApi,
    private val userId:Int,
    private val type:String,
    private val locationState: LocationState
): PagingStrategy<MediaPostsDto, MediaPost, Int> {
    override suspend fun fetch(cursor: Int?): Response<BaseApiResponse<MediaPostsDto>> {
        return api.getMedia(
            userid = userId,
            type = type,
            mediaid = cursor,
            latitude = locationState.latitude,
            longitude = locationState.longitude
        )
    }

    override fun mapToDomain(data: MediaPostsDto): List<MediaPost> {

        return data.mediaPosts.map{ it.toDomain() }
    }

    override fun extractNextCursor(items: List<MediaPost>, pageSize: Int): Int? {
        return  items.lastOrNull()?.id?.takeIf {
            items.size == pageSize
        }
    }


}