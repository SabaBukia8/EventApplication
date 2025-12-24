package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(): Flow<Resource<List<Category>>> {
        return repository.getEventTypes()
    }
}
