package com.example.weatherapp.data.repository

import com.example.weatherapp.data.db.ForecastEntity
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.flow.Flow

// Interface for the weather data repository.
// This follows the Dependency Inversion Principle, allowing for easier testing and maintenance.
interface WeatherRepository {
    fun getCurrentWeather(cityName: String, apiKey: String): Flow<Resource<WeatherEntity>>
    fun getForecast(cityName: String, apiKey: String): Flow<Resource<List<ForecastEntity>>>
}