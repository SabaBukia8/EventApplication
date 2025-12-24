package com.example.eventapplication.data.remote.interceptor

import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.domain.util.preferences.PreferenceKeys
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val path = originalRequest.url.encodedPath.lowercase()
        if (path.contains("auth/login") ||
            path.contains("auth/register") ||
            path.contains("departments")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            dataStoreManager.getPreference(PreferenceKeys.ACCESS_TOKEN, "")
                .firstOrNull()
        }

        return if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
