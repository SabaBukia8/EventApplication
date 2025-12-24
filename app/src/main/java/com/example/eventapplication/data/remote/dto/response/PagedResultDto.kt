package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResultDto<T>(
    val items: List<T>,
    @SerialName("currentPage") val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int,
    @SerialName("hasPrevious") val hasPreviousPage: Boolean,
    @SerialName("hasNext") val hasNextPage: Boolean
)
