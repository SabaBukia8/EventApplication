package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.DepartmentDto
import com.example.eventapplication.domain.model.Department

fun DepartmentDto.toDomain(): Department {
    return Department(id = id, name = name)
}

fun List<DepartmentDto>.toDomain(): List<Department> {
    return map { it.toDomain() }
}
