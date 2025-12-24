package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.PagedResultDto
import com.example.eventapplication.domain.model.PaginatedResult

fun <T, R> PagedResultDto<T>.toDomain(mapper: (T) -> R): PaginatedResult<R> {
    return PaginatedResult(
        items = items.map(mapper),
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalCount = totalCount,
        totalPages = totalPages,
        hasPreviousPage = hasPreviousPage,
        hasNextPage = hasNextPage
    )
}
