package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.domain.util.preferences.PreferenceKeys
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRememberedEmailUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke(): Pair<Boolean, String> {
        val rememberMe = dataStoreManager.getPreference(PreferenceKeys.REMEMBER_ME_ENABLED, false).first()
        val email = if (rememberMe) {
            dataStoreManager.getPreference(PreferenceKeys.REMEMBERED_EMAIL, "").first()
        } else {
            ""
        }
        return rememberMe to email
    }
}
