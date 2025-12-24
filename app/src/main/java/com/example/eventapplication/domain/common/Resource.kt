package com.example.eventapplication.domain.common

import com.example.eventapplication.domain.model.NetworkError

sealed class Resource<out T> {
    data class Loader(val isLoading: Boolean) : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val error: NetworkError) : Resource<Nothing>()
}