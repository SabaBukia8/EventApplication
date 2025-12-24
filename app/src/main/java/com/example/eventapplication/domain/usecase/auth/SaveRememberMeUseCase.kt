package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.domain.util.preferences.PreferenceKeys
import javax.inject.Inject

class SaveRememberMeUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke(rememberMe: Boolean, email: String) {
        dataStoreManager.setPreference(PreferenceKeys.REMEMBER_ME_ENABLED, rememberMe)
        if (rememberMe) {
            dataStoreManager.setPreference(PreferenceKeys.REMEMBERED_EMAIL, email)
        } else {
            dataStoreManager.removePreference(PreferenceKeys.REMEMBERED_EMAIL)
        }
    }
}
