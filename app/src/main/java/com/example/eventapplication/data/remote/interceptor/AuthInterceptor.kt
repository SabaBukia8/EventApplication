package com.example.eventapplication.data.remote.interceptor

import android.util.Log
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
        val path = originalRequest.url.encodedPath

        if (path.lowercase().contains("auth/login") ||
            path.lowercase().contains("auth/register") ||
            path.lowercase().contains("departments")
        ) {
            Log.d("AuthInterceptor", "Bypassing auth for: $path")
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            dataStoreManager.getPreference(PreferenceKeys.ACCESS_TOKEN, "")
                .firstOrNull()
        }

        Log.d(
            "AuthInterceptor",
            "Request: $path | Token: ${if (token.isNullOrEmpty()) "MISSING" else "Present"}"
        )

        return if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            Log.w("AuthInterceptor", "⚠ No token for: $path")
            chain.proceed(originalRequest)
        }
    }
}
