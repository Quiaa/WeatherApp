package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.Response

// Implementation of the WeatherRepository.
// It takes the WeatherApiService as a dependency.
class WeatherRepositoryImpl(private val apiService: WeatherApiService) : WeatherRepository {

    override suspend fun getCurrentWeather(cityName: String, apiKey: String): Response<WeatherResponse> {
        return apiService.getCurrentWeather(cityName, apiKey)
    }
}