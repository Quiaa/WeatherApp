package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.Response

// Interface for the weather data repository.
// This follows the Dependency Inversion Principle, allowing for easier testing and maintenance.
interface WeatherRepository {
    suspend fun getCurrentWeather(cityName: String, apiKey: String): Response<WeatherResponse>
}