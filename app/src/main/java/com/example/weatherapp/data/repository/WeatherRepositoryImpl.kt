package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.db.ForecastDao
import com.example.weatherapp.data.db.ForecastEntity
import com.example.weatherapp.data.db.WeatherDao
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.location.LocationTracker
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao,
    private val locationTracker: LocationTracker
) : WeatherRepository {

    override fun getCurrentWeather(cityName: String, apiKey: String): Flow<Resource<WeatherEntity>> = flow {
        emit(Resource.Loading())
        val cachedWeather = weatherDao.getWeatherByCity(cityName).first()

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
                weatherDao.insertWeather(weatherEntity)
                emit(Resource.Success(weatherEntity))
            } else {
                emit(Resource.Error("API Error: ${remoteWeather.message()}", cachedWeather))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network Error: Could not connect to the server.", cachedWeather))
        }
    }

    override fun getForecast(cityName: String, apiKey: String): Flow<Resource<List<ForecastEntity>>> = flow {
        emit(Resource.Loading())
        val cachedForecasts = forecastDao.getForecastsByCity(cityName).first()

        try {
            val remoteForecast = apiService.getForecast(cityName, apiKey)
            if (remoteForecast.isSuccessful && remoteForecast.body() != null) {
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
                emit(Resource.Error("API Error: ${remoteForecast.message()}", cachedForecasts))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network Error: Could not connect to the server.", cachedForecasts))
        }
    }

    override suspend fun fetchCityNameForCurrentLocation(): Resource<String> {
        val location = locationTracker.getCurrentLocation() ?: return Resource.Error("Couldn't retrieve location.")

        try {
            val weatherResponse = apiService.getCurrentWeatherByCoord(location.latitude, location.longitude, Constants.API_KEY)
            if (!weatherResponse.isSuccessful || weatherResponse.body() == null) {
                return Resource.Error("API Error: Could not get weather data for location.")
            }
            val weatherBody = weatherResponse.body()!!
            val cityName = weatherBody.name

            weatherDao.insertWeather(weatherBody.toWeatherEntity())

            val forecastResponse = apiService.getForecastByCoord(location.latitude, location.longitude, Constants.API_KEY)
            if(forecastResponse.isSuccessful && forecastResponse.body() != null) {
                forecastDao.deleteForecastsByCity(cityName)
                forecastDao.insertForecasts(forecastResponse.body()!!.toForecastEntityList(cityName))
            }

            return Resource.Success(cityName)

        } catch (e: Exception) {
            return Resource.Error("Network Error: ${e.message}")
        }
    }

    private fun WeatherResponse.toWeatherEntity(): WeatherEntity {
        return WeatherEntity(
            cityName = this.name,
            temperature = this.main.temp,
            description = this.weather.firstOrNull()?.description ?: "",
            iconCode = this.weather.firstOrNull()?.icon ?: "",
            timestamp = System.currentTimeMillis()
        )
    }

    private fun ForecastResponse.toForecastEntityList(cityName: String): List<ForecastEntity> {
        return this.list.map {
            ForecastEntity(
                ownerCityName = cityName,
                dt = it.dt,
                temperature = it.main.temp,
                description = it.weather.firstOrNull()?.description ?: "",
                iconCode = it.weather.firstOrNull()?.icon ?: ""
            )
        }
    }
}