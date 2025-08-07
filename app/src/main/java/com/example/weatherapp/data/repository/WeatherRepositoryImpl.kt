package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.db.ForecastDao
import com.example.weatherapp.data.db.ForecastEntity
import com.example.weatherapp.data.db.WeatherDao
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val dao: WeatherDao,
    private val forecastDao: ForecastDao
) : WeatherRepository {

    override fun getCurrentWeather(cityName: String, apiKey: String): Flow<Resource<WeatherEntity>> = flow {
        emit(Resource.Loading())

        val cachedWeather = dao.getWeatherByCity(cityName).first()

        try {
            val remoteWeather = apiService.getCurrentWeather(cityName, apiKey)
            if (remoteWeather.isSuccessful && remoteWeather.body() != null) {
                val weatherData = remoteWeather.body()!!
                val weatherEntity = WeatherEntity(
                    cityName = weatherData.name,
                    temperature = weatherData.main.temp,
                    description = weatherData.weather.firstOrNull()?.description ?: "N/A",
                    iconCode = weatherData.weather.firstOrNull()?.icon ?: "",
                    timestamp = System.currentTimeMillis()
                )
                dao.insertWeather(weatherEntity)

                emit(Resource.Success(weatherEntity))

            } else {
                // Network call failed, but we might have cached data.
                if (cachedWeather != null) {
                    emit(Resource.Success(cachedWeather)) // Show stale data if network fails
                } else {
                    emit(Resource.Error("API Error: ${remoteWeather.message()}"))
                }
            }
        } catch (e: Exception) {
            // Network exception, but we might have cached data.
            if (cachedWeather != null) {
                emit(Resource.Success(cachedWeather)) // Show stale data if network fails
            } else {
                emit(Resource.Error("Network Error: Could not connect to the server."))
            }
        }
    }
    override fun getForecast(cityName: String, apiKey: String): Flow<Resource<List<ForecastEntity>>> = flow {
        emit(Resource.Loading())

        //We won't first retrieve the cached data and then emit it, because if the network request is successful, we'll display fresh data.
        // If there's an error, we'll use the cache.
        val cachedForecasts = forecastDao.getForecastsByCity(cityName).first()

        try {
            val remoteForecast = apiService.getForecast(cityName, apiKey)
            if (remoteForecast.isSuccessful && remoteForecast.body() != null) {
                // Before recording data from the network, old data is cleared.
                forecastDao.deleteForecastsByCity(cityName)

                val forecastEntities = remoteForecast.body()!!.list.map { forecastItem ->
                    ForecastEntity(
                        ownerCityName = cityName,
                        dt = forecastItem.dt,
                        temperature = forecastItem.main.temp,
                        description = forecastItem.weather.firstOrNull()?.description ?: "N/A",
                        iconCode = forecastItem.weather.firstOrNull()?.icon ?: ""
                    )
                }
                forecastDao.insertForecasts(forecastEntities)
                emit(Resource.Success(forecastEntities))
            } else {
                // Network error but cache full
                if (cachedForecasts.isNotEmpty()) {
                    emit(Resource.Success(cachedForecasts))
                } else {
                    emit(Resource.Error("API Error: ${remoteForecast.message()}"))
                }
            }
        } catch (e: Exception) {
            // Network exception but cache full
            if (cachedForecasts.isNotEmpty()) {
                emit(Resource.Success(cachedForecasts))
            } else {
                emit(Resource.Error("Network Error: Could not connect to the server."))
            }
        }
    }
}