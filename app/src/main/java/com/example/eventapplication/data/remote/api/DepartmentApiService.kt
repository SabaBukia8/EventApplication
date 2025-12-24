package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.response.DepartmentDto
import retrofit2.http.GET

interface DepartmentApiService {
    @GET("api/departments")
    suspend fun getDepartments(): List<DepartmentDto>
}
