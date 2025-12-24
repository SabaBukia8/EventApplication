package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.DepartmentApiService
import com.example.eventapplication.domain.model.Department
import com.example.eventapplication.domain.repository.DepartmentRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DepartmentRepositoryImpl @Inject constructor(
    private val departmentApiService: DepartmentApiService,
    private val handleResponse: HandleResponse
) : DepartmentRepository {
    override fun getDepartments(): Flow<Resource<List<Department>>> {
        return handleResponse.safeApiCall {
            departmentApiService.getDepartments().toDomain()
        }
    }
}
