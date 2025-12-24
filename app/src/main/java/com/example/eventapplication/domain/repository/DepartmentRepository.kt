package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.model.Department
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface DepartmentRepository {
    fun getDepartments(): Flow<Resource<List<Department>>>
}
