package com.example.eventapplication.di

import com.example.eventapplication.BuildConfig
import com.example.eventapplication.data.common.NetworkConstants
import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.data.remote.api.DepartmentApiService
import com.example.eventapplication.data.remote.api.EventDetailsApiService
import com.example.eventapplication.data.remote.api.EventRegistrationStatusApiService
import com.example.eventapplication.data.remote.api.EventsApiService
import com.example.eventapplication.data.remote.api.LoginApiService
import com.example.eventapplication.data.remote.api.NotificationsApiService
import com.example.eventapplication.data.remote.api.RegistrationApiService
import com.example.eventapplication.data.remote.api.UserApiService
import com.example.eventapplication.data.remote.api.UserRegistrationsApiService
import com.example.eventapplication.data.remote.interceptor.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        dataStoreManager: DataStoreManager
    ): AuthInterceptor {
        return AuthInterceptor(dataStoreManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(NetworkConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)



        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideRegistrationApiService(retrofit: Retrofit): RegistrationApiService {
        return retrofit.create(RegistrationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApiService(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDepartmentApiService(retrofit: Retrofit): DepartmentApiService {
        return retrofit.create(DepartmentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventDetailsApiService(retrofit: Retrofit): EventDetailsApiService {
        return retrofit.create(EventDetailsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventsApiService(retrofit: Retrofit): EventsApiService {
        return retrofit.create(EventsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationsApiService(retrofit: Retrofit): NotificationsApiService {
        return retrofit.create(NotificationsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRegistrationsApiService(retrofit: Retrofit): UserRegistrationsApiService {
        return retrofit.create(UserRegistrationsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventRegistrationStatusApiService(retrofit: Retrofit): EventRegistrationStatusApiService {
        return retrofit.create(EventRegistrationStatusApiService::class.java)
    }
}
