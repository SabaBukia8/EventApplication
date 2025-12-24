package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.UserApiService
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.AuthResult
import com.example.eventapplication.domain.model.User
import com.example.eventapplication.domain.repository.TokenRepository
import com.example.eventapplication.domain.util.preferences.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val userApiService: UserApiService,
    private val handleResponse: HandleResponse
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

            // DEBUG LOG to see what is happening
            android.util.Log.d("TokenRepo", "Listening for User updates...")

            kotlinx.coroutines.flow.combine(userId, email, fullName, role) { id, em, name, r ->
                // Create User object whenever any data changes
                User(id = id, email = em, fullName = name, role = r)
            }.collect { user ->
                // Log the value we found (so we know if it's empty)
                android.util.Log.d("TokenRepo", "DataStore Update -> Name: '${user.fullName}'")

                if (user.fullName.isNotEmpty()) {
                    // FOUND THE USER! Emit Success.
                    emit(Resource.Success(user))
                } else {
                    // Name is empty -> Emit Guest User
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
                // REMOVED: emit(Resource.Loader(false))
                // Why? Because 'collect' runs forever. If we emit Loader here,
                // the UI thinks "Loading finished but no data" and reverts to Guest.
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            // Don't catch cancellation - this is normal Flow cancellation
            throw e
        } catch (e: Exception) {
            android.util.Log.e("TokenRepo", "Error reading user", e)
            // Fallback for crashes
            emit(Resource.Success(User(0, "", "Guest User", "")))
        }
    }
}
