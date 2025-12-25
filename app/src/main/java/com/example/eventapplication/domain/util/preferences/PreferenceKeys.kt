package com.example.eventapplication.domain.util.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_FULL_NAME = stringPreferencesKey("user_full_name")
    val USER_ROLE = stringPreferencesKey("user_role")


    val REMEMBER_ME_ENABLED = booleanPreferencesKey("remember_me_enabled")
    val REMEMBERED_EMAIL = stringPreferencesKey("remembered_email")
    val SESSION_SHOULD_PERSIST = booleanPreferencesKey("session_should_persist")
}
