package com.example.eventapplication

import android.app.Application
import com.example.eventapplication.domain.usecase.auth.CheckAndClearSessionUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class EventApplication : Application() {

    @Inject
    lateinit var checkAndClearSessionUseCase: CheckAndClearSessionUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        // Check session persistence on app cold start
        // This runs before any Fragment/ViewModel is created
        applicationScope.launch {
            checkAndClearSessionUseCase()
        }
    }
}