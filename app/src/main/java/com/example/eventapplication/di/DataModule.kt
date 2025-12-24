package com.example.eventapplication.di

import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.data.local.datastore.DataStoreManagerImpl
import com.example.eventapplication.data.repository.DepartmentRepositoryImpl
import com.example.eventapplication.data.repository.EventDetailsRepositoryImpl
import com.example.eventapplication.data.repository.EventsRepositoryImpl
import com.example.eventapplication.data.repository.LoginRepositoryImpl
import com.example.eventapplication.data.repository.NotificationsRepositoryImpl
import com.example.eventapplication.data.repository.RegisterRepositoryImpl
import com.example.eventapplication.data.repository.TokenRepositoryImpl
import com.example.eventapplication.data.repository.UserRegistrationsRepositoryImpl
import com.example.eventapplication.domain.repository.DepartmentRepository
import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.repository.EventsRepository
import com.example.eventapplication.domain.repository.LoginRepository
import com.example.eventapplication.domain.repository.NotificationsRepository
import com.example.eventapplication.domain.repository.RegisterRepository
import com.example.eventapplication.domain.repository.TokenRepository
import com.example.eventapplication.domain.repository.UserRegistrationsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindDataStoreManager(
        impl: DataStoreManagerImpl
    ): DataStoreManager

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        impl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        impl: RegisterRepositoryImpl
    ): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        impl: TokenRepositoryImpl
    ): TokenRepository

    @Binds
    @Singleton
    abstract fun bindDepartmentRepository(
        impl: DepartmentRepositoryImpl
    ): DepartmentRepository

    @Binds
    @Singleton
    abstract fun bindEventDetailsRepository(
        impl: EventDetailsRepositoryImpl
    ): EventDetailsRepository

    @Binds
    @Singleton
    abstract fun bindEventsRepository(
        impl: EventsRepositoryImpl
    ): EventsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        impl: NotificationsRepositoryImpl
    ): NotificationsRepository

    @Binds
    @Singleton
    abstract fun bindUserRegistrationsRepository(
        impl: UserRegistrationsRepositoryImpl
    ): UserRegistrationsRepository
}
