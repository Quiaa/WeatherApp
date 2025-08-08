package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.db.ForecastEntity
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.WeatherResponse

fun WeatherResponse.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.name,
        temperature = this.main.temp,
        description = this.weather.firstOrNull()?.description ?: "N/A",
        iconCode = this.weather.firstOrNull()?.icon ?: "",
        timestamp = System.currentTimeMillis()
    )
}

fun ForecastResponse.toEntityList(cityName: String): List<ForecastEntity> {
    return this.list.map { forecastItem ->
        ForecastEntity(
            ownerCityName = cityName,
            dt = forecastItem.dt,
            temperature = forecastItem.main.temp,
            description = forecastItem.weather.firstOrNull()?.description ?: "N/A",
            iconCode = forecastItem.weather.firstOrNull()?.icon ?: ""
        )
    }
}