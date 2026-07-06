package com.androiddev.domain.model

sealed interface PostQuery {

    data class New(
        val latitude: Double?,
        val longitude: Double?
    ) : PostQuery

    data class User(
        val userId: Int,
        val latitude: Double?,
        val longitude: Double?
    ) : PostQuery

    data class Near(
        val distance: Int,
        val latitude: Double,
        val longitude: Double
    ) : PostQuery

    data class NewTag(
        val tagId: Int,
        val latitude: Double?,
        val longitude: Double?
    ) : PostQuery

}