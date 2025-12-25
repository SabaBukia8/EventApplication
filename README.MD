# EventHub

**EventHub** is a modern Android application built to manage event discovery, registration, and user engagement. It leverages **Clean Architecture** and **MVI** patterns to ensure a scalable and testable codebase.

---

## 📱 Architecture Overview

The application follows the principles of **Clean Architecture** to separate concerns and maintain independence between layers:

* **Presentation Layer**: Implements **MVI**. It consists of Fragments, Activities, ViewModels and intents that observe state changes and handle UI interactions.
* **Domain Layer**: Contains the core business logic, including **Use Cases** and **Repository Interfaces**. This layer is pure Kotlin and independent of the Android framework.
* **Data Layer**: Manages data retrieval and storage. It implements Repositories and handles communication with the backend via **Retrofit**.
* **Dependency Injection**: Uses **Hilt** for robust dependency management across the app.

---

## 🛠 Tech Stack

* **Language**: Kotlin
* **Concurrency**: Coroutines & Flow
* **Networking**:
    * **Retrofit**: Type-safe HTTP client.
    * **OkHttp**: HTTP client for interceptors and logging.
    * **Kotlin Serialization**: JSON parsing.
* **DI**: Hilt (Dagger).
* **Local Storage**: DataStore (Preferences) for token management.

---

## 🔗 API Endpoints

The application interacts with a RESTful backend using the following services:

### 🔐 Authentication
* **Login**: `POST /api/Auth/login`
* **Register**: `POST /api/Auth/register`
* **OTP**: `POST /api/Auth/send-otp` & `POST /api/Auth/verify-otp`

### 📅 Events
* **Browse**: `GET /api/Events` (Supports filtering by type, location, date, and pagination)
* **Details**: `GET /api/events/{id}`
* **Trending**: `GET /api/Events/trending`
* **Categories**: `GET /api/Events/types`

### 🎟 Registrations
* **Join**: `POST /api/Registrations`
* **Cancel**: `DELETE /api/Registrations/event/{eventId}/cancel`
* **My Events**: `GET /api/Registrations/user/me`

### 🔔 Notifications
* **Fetch**: `GET /api/Notifications/my-notifications`
* **Actions**: Mark specific or all notifications as read.

### 🏢 Departments
* **List**: `GET /api/departments`

---

## ⚠️ Known Limitations
2.  **Offline Access**: The app requires an active internet connection. There is no local database (Room) caching for events or registration data.
3.  **Security Configuration**: The current debug build configuration allows all SSL certificates for `localhost`, which is intended strictly for development/testing environments.

---

## 🚀 Future Improvements
* **Offline Caching**: Implement **Room Database** to cache events and user registrations for offline viewing.
* **Token Refresh**: Add automatic token refresh logic within the `AuthInterceptor` to handle expired sessions seamlessly.
* **Testing Strategy**: Expand Unit and UI test coverage for ViewModels and Repositories.