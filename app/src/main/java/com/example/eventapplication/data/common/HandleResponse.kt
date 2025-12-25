package com.example.eventapplication.data.common

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class HandleResponse @Inject constructor() {

    fun <T> safeApiCall(apiCall: suspend () -> T): Flow<Resource<T>> = flow {
        emit(Resource.Loader(isLoading = true))
        try {
            val response = apiCall()
            emit(Resource.Success(data = response))
        } catch (e: Exception) {
            val error = when (e) {
                is SocketTimeoutException -> NetworkError.Timeout
                is IOException -> NetworkError.NoInternet
                is HttpException -> {
                    val errorMessage = try {
                        e.response()?.errorBody()?.string()?.let { errorBody ->
                            val messageRegex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                            messageRegex.find(errorBody)?.groupValues?.get(1)
                        }
                    } catch (ex: Exception) {
                        null
                    }

                    when (e.code()) {
                        401 -> NetworkError.Unauthorized
                        403 -> NetworkError.Forbidden
                        404 -> NetworkError.NotFound
                        409 -> NetworkError.Unknown(
                            errorMessage ?: "Conflict: Resource already exists"
                        )

                        in 500..599 -> NetworkError.ServerError
                        else -> NetworkError.Unknown(errorMessage ?: e.message())
                    }
                }

                else -> NetworkError.Unknown(e.message)
            }
            emit(Resource.Error(error))
        } finally {
            emit(Resource.Loader(isLoading = false))
        }
    }.flowOn(Dispatchers.IO)
}