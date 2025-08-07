package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// This interface defines the API endpoints for OpenWeatherMap.
interface WeatherApiService {

    // Fetches the current weather data for a specific city.
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = Constants.DEFAULT_UNITS // To get temperature in Celsius
    ): Response<WeatherResponse>
}