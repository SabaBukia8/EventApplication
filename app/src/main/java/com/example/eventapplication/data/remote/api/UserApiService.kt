package com.example.eventapplication.data.remote.api

interface UserApiService {

    // REMOVED: No API endpoint exists to fetch current user profile
    // The backend doesn't provide GET /api/auth/me or similar endpoint
    // User data can ONLY be obtained during login (POST /api/Auth/login)
    //
    // If this is needed in the future, the endpoint should be discussed with backend team
    // Expected endpoint: GET /api/users/me or GET /api/auth/me
    //
    // @GET("api/users/me")
    // suspend fun getCurrentUser(): UserResponseDto
}
