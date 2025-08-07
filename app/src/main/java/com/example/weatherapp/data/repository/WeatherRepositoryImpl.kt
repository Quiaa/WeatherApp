package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.util.Resource
import java.io.IOException

// Implementation of the WeatherRepository.
// It takes the WeatherApiService as a dependency.
class WeatherRepositoryImpl(private val apiService: WeatherApiService) : WeatherRepository {

    override suspend fun getCurrentWeather(cityName: String, apiKey: String): Resource<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeather(cityName, apiKey)
            if (response.isSuccessful) {
                // If the response body is not null, wrap it in a Success resource.
                response.body()?.let {
                    return Resource.Success(it)
                }
                    ?: Resource.Error("Response body is null") // Handle the unlikely case of a null body.
            } else {
                // If the server responded with an error code.
                Resource.Error("API Error: ${response.message()}")
            }
        } catch (e: IOException) {
            // Handles network errors, like no internet connection.
            Resource.Error("Network Error: Please check your internet connection.")
        } catch (e: Exception) {
            // Handles other unexpected errors.
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}