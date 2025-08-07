package com.example.weatherapp.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // We'll use this class to represent a successful network request.
    class Success<T>(data: T) : Resource<T>(data)

    // We'll use this to represent a failed network request.
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    // We'll use this to show a loading indicator (e.g., ProgressBar)
    class Loading<T> : Resource<T>()
}