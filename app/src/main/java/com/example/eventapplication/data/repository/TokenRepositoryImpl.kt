package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.AuthResult
import com.example.eventapplication.domain.model.User
import com.example.eventapplication.domain.repository.TokenRepository
import com.example.eventapplication.domain.util.preferences.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : TokenRepository {

    override suspend fun saveToken(token: String) {
        dataStoreManager.setPreference(PreferenceKeys.ACCESS_TOKEN, token)
    }

    override fun getToken(): Flow<String?> {
        return dataStoreManager.getPreference(PreferenceKeys.ACCESS_TOKEN, "")
            .map { if (it.isEmpty()) null else it }
    }

    override suspend fun clearToken() {
        dataStoreManager.removePreference(PreferenceKeys.ACCESS_TOKEN)
        dataStoreManager.removePreference(PreferenceKeys.USER_ID)
        dataStoreManager.removePreference(PreferenceKeys.USER_EMAIL)
        dataStoreManager.removePreference(PreferenceKeys.USER_FULL_NAME)
        dataStoreManager.removePreference(PreferenceKeys.USER_ROLE)
    }

    override suspend fun saveUserData(authResult: AuthResult, email: String) {
        android.util.Log.d("TokenRepo", "Saving User: ${authResult.fullName}, Email: $email")
        dataStoreManager.setPreference(PreferenceKeys.USER_ID, authResult.userId.toString())
        dataStoreManager.setPreference(PreferenceKeys.USER_EMAIL, email)
        dataStoreManager.setPreference(PreferenceKeys.USER_FULL_NAME, authResult.fullName)
        dataStoreManager.setPreference(PreferenceKeys.USER_ROLE, authResult.role)
    }

    override fun getCurrentUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loader(true))

        try {
            val userId = dataStoreManager.getPreference(PreferenceKeys.USER_ID, "").map {
                it.toIntOrNull() ?: 0
            }
            val email = dataStoreManager.getPreference(PreferenceKeys.USER_EMAIL, "")
            val fullName = dataStoreManager.getPreference(PreferenceKeys.USER_FULL_NAME, "")
            val role = dataStoreManager.getPreference(PreferenceKeys.USER_ROLE, "")

            android.util.Log.d("TokenRepo", "Listening for User updates...")

            kotlinx.coroutines.flow.combine(userId, email, fullName, role) { id, em, name, r ->
                User(id = id, email = em, fullName = name, role = r)
            }.collect { user ->
                android.util.Log.d("TokenRepo", "DataStore Update -> Name: '${user.fullName}'")

                if (user.fullName.isNotEmpty()) {
                    emit(Resource.Success(user))
                } else {
                    emit(
                        Resource.Success(
                            User(
                                id = 0,
                                email = "",
                                fullName = "Guest User",
                                role = ""
                            )
                        )
                    )
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            android.util.Log.e("TokenRepo", "Error reading user", e)
            emit(Resource.Success(User(0, "", "Guest User", "")))
        }
    }

    override suspend fun setSessionPersistence(shouldPersist: Boolean) {
        dataStoreManager.setPreference(PreferenceKeys.SESSION_SHOULD_PERSIST, shouldPersist)
    }

    override suspend fun shouldSessionPersist(): Boolean {
        return dataStoreManager.getPreference(PreferenceKeys.SESSION_SHOULD_PERSIST, false).map { it }.first()
    }
}
