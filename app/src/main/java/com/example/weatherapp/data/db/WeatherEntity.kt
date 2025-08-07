package com.example.weatherapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherEntity(
    @PrimaryKey
    val cityName: String,
    val temperature: Double,
    val description: String,
    val iconCode: String,
    val timestamp: Long // To check how old the data is
)