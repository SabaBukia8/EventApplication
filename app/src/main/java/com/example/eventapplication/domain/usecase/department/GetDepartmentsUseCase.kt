package com.example.eventapplication.domain.usecase.department

import com.example.eventapplication.domain.model.Department
import com.example.eventapplication.domain.repository.DepartmentRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDepartmentsUseCase @Inject constructor(
    private val departmentRepository: DepartmentRepository
) {
    operator fun invoke(): Flow<Resource<List<Department>>> {
        return departmentRepository.getDepartments()
    }
}
